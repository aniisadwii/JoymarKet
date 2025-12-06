package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.CartItem;
import model.Customer;
import model.Order;
import model.Promo;
import util.Connect;
import util.Session;

public class OrderHandler {
    
    private Connect db = Connect.getInstance();
    private CartItemHandler cartHandler = new CartItemHandler(); 

    // GANTI PARAMETER SESUAI CLASS DIAGRAM: checkout(idOrder, idPromo)
    // idCustomer kita ambil dari Session (implisit)
    public String checkout(String idOrder, String idPromo) {
        
        // 1. Ambil User dari Session (Karena diagram gak minta idCustomer di parameter)
        Customer user = (Customer) Session.getInstance().getUser();
        String idCustomer = user.getIdUser();

        List<CartItem> cart = cartHandler.getCartItems(idCustomer);
        if (cart.isEmpty()) return "Cart is empty";

        // 2. Hitung Total & Validasi Stok
        double totalAmount = 0;
        for (CartItem item : cart) {
            int currentStock = getProductStock(item.getIdProduct());
            if (currentStock < item.getQuantity()) {
                return "Stock changed! " + item.getProductName() + " not enough.";
            }
            totalAmount += item.getTotal();
        }

        // 3. Cek Promo (Parameter idPromo di sini adalah KODE PROMO dari inputan view)
        // Note: Diagram tulisnya 'idPromo', tapi context-nya input user, jadi anggap itu Code.
        String finalIdPromo = null; // Ini ID Promo buat database
        
        if (idPromo != null && !idPromo.isEmpty()) {
            Promo promo = getPromo(idPromo); // Cek based on Code
            if (promo == null) return "Invalid Promo Code!";
            
            double discount = totalAmount * (promo.getDiscountPercentage() / 100.0);
            totalAmount -= discount;
            finalIdPromo = promo.getIdPromo(); // Simpan ID aslinya
        }

        // 4. Cek Saldo
        if (user.getBalance() < totalAmount) {
            return "Balance not sufficient! (Total: " + totalAmount + ")";
        }

        // --- TRANSAKSI ---
        // idOrder sudah dikirim dari View, jadi kita pake itu.
        String idPromoVal = (finalIdPromo == null) ? "NULL" : "'" + finalIdPromo + "'";
        
        // Insert Header
        String queryHeader = String.format("INSERT INTO OrderHeaders (idOrder, idCustomer, idPromo, totalAmount, status, orderedAt) VALUES ('%s', '%s', %s, %f, 'Pending', NOW())",
                idOrder, idCustomer, idPromoVal, totalAmount);
        db.execUpdate(queryHeader);

        // Insert Detail & Potong Stok
        for (CartItem item : cart) {
            String queryDetail = String.format("INSERT INTO OrderDetails VALUES ('%s', '%s', %d)", idOrder, item.getIdProduct(), item.getQuantity());
            db.execUpdate(queryDetail);
            String updateStock = String.format("UPDATE Products SET stock = stock - %d WHERE idProduct = '%s'", item.getQuantity(), item.getIdProduct());
            db.execUpdate(updateStock);
        }

        // Potong Saldo
        double newBalance = user.getBalance() - totalAmount;
        String updateBalance = String.format("UPDATE Customers SET balance = %f WHERE idCustomer = '%s'", newBalance, idCustomer);
        db.execUpdate(updateBalance);
        user.setBalance(newBalance);

        // Clear Cart
        String clearCart = "DELETE FROM CartItems WHERE idCustomer = '" + idCustomer + "'";
        db.execUpdate(clearCart);

        return "Success";
    }
    
    public List<Order> getCustomerOrders(String idCustomer) {
        List<Order> orders = new ArrayList<>();
        // Ambil semua order milik customer ini, urutkan dari yang terbaru
        String query = String.format("SELECT * FROM OrderHeaders WHERE idCustomer = '%s' ORDER BY orderedAt DESC", idCustomer);
        ResultSet rs = db.execQuery(query);
        
        try {
            while (rs.next()) {
                orders.add(new Order(
                    rs.getString("idOrder"),
                    rs.getString("idCustomer"),
                    rs.getDouble("totalAmount"),
                    rs.getString("status"),
                    rs.getTimestamp("orderedAt")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
    
 // === METHOD BARU: Sesuai Class Diagram "getAllOrders" ===
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        // Ambil SEMUA data dari tabel OrderHeaders
        String query = "SELECT * FROM OrderHeaders";
        ResultSet rs = db.execQuery(query);
        
        try {
            while (rs.next()) {
                orders.add(new Order(
                    rs.getString("idOrder"),
                    rs.getString("idCustomer"),
                    rs.getDouble("totalAmount"),
                    rs.getString("status"),
                    rs.getTimestamp("orderedAt")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    // Helper methods (Copy paste aja yang lama)
    private Promo getPromo(String code) {
        String query = "SELECT * FROM Promos WHERE code = '" + code + "'";
        ResultSet rs = db.execQuery(query);
        try {
            if (rs.next()) {
                return new Promo(rs.getString("idPromo"), rs.getString("code"), rs.getDouble("discountPercentage"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    
    private int getProductStock(String productId) {
        String query = "SELECT stock FROM Products WHERE idProduct = '" + productId + "'";
        ResultSet rs = db.execQuery(query);
        try {
            if (rs.next()) return rs.getInt("stock");
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }
}