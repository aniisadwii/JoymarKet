package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.CartItem;
import model.Customer;
import model.OrderHeader;
import model.Promo;
import util.Connect;
import util.Session;

public class OrderHandler {
    
    private Connect db = Connect.getInstance();
    private CartItemHandler cartHandler = new CartItemHandler(); 
    private PromoHandler promoHandler = new PromoHandler();

    // method utama untuk proses checkout, parameter sesuai diagram (idOrder, idPromo)
    public String checkout(String idOrder, String idPromo) {
        
        // 1. ambil user dari session
        Customer user = (Customer) Session.getInstance().getUser();
        String idCustomer = user.getIdUser();

        // ambil barang dari keranjang
        List<CartItem> cart = cartHandler.getCartItems(idCustomer);
        if (cart.isEmpty()) return "Cart is empty";

        // 2. hitung total & validasi stok terakhir
        double totalAmount = 0;
        for (CartItem item : cart) {
            int currentStock = getProductStock(item.getIdProduct());
            if (currentStock < item.getQuantity()) {
                return "Stock changed! " + item.getProductName() + " not enough.";
            }
            totalAmount += item.getTotal();
        }

        // 3. cek validitas kode promo kalau user input
        String finalIdPromo = null; 
        
        if (idPromo != null && !idPromo.isEmpty()) {
            Promo promo = promoHandler.getPromo(idPromo);
            if (promo == null) return "Invalid Promo Code!";
            
            // hitung diskon
            double discount = totalAmount * (promo.getDiscountPercentage() / 100.0);
            totalAmount -= discount;
            finalIdPromo = promo.getIdPromo(); 
        }

        // 4. cek saldo user cukup atau ngga
        if (user.getBalance() < totalAmount) {
            return "Balance not sufficient! (Total: " + totalAmount + ")";
        }

        // --- mulai transaksi database ---
        
        String idPromoVal = (finalIdPromo == null) ? "NULL" : "'" + finalIdPromo + "'";
        
        // a. insert header order
        String queryHeader = String.format("INSERT INTO OrderHeaders (idOrder, idCustomer, idPromo, totalAmount, status, orderedAt) VALUES ('%s', '%s', %s, %f, 'Pending', NOW())",
                idOrder, idCustomer, idPromoVal, totalAmount);
        db.execUpdate(queryHeader);

        // b. insert detail order & kurangi stok
        for (CartItem item : cart) {
            String queryDetail = String.format("INSERT INTO OrderDetails VALUES ('%s', '%s', %d)", idOrder, item.getIdProduct(), item.getQuantity());
            db.execUpdate(queryDetail);
            String updateStock = String.format("UPDATE Products SET stock = stock - %d WHERE idProduct = '%s'", item.getQuantity(), item.getIdProduct());
            db.execUpdate(updateStock);
        }

        // c. potong saldo user
        double newBalance = user.getBalance() - totalAmount;
        String updateBalance = String.format("UPDATE Customers SET balance = %f WHERE idCustomer = '%s'", newBalance, idCustomer);
        db.execUpdate(updateBalance);
        user.setBalance(newBalance);

        // d. hapus item dari keranjang
        String clearCart = "DELETE FROM CartItems WHERE idCustomer = '" + idCustomer + "'";
        db.execUpdate(clearCart);

        return "Success";
    }
    
    // method ini untuk ambil history order customer, urut dari yg terbaru
    public List<OrderHeader> getCustomerOrders(String idCustomer) {
        List<OrderHeader> orders = new ArrayList<>();
        String query = String.format("SELECT * FROM OrderHeaders WHERE idCustomer = '%s' ORDER BY orderedAt DESC", idCustomer);
        ResultSet rs = db.execQuery(query);
        
        try {
            while (rs.next()) {
                orders.add(new OrderHeader(
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
    
    // method ini untuk ambil detail 1 order spesifik buat validasi/view
    public OrderHeader getCustomerOrderHeader(String idOrder, String idCustomer) {
        String query = String.format("SELECT * FROM OrderHeaders WHERE idOrder = '%s' AND idCustomer = '%s'", idOrder, idCustomer);
        ResultSet rs = db.execQuery(query);
        
        try {
            if (rs.next()) {
                return new OrderHeader(
                    rs.getString("idOrder"),
                    rs.getString("idCustomer"),
                    rs.getDouble("totalAmount"),
                    rs.getString("status"),
                    rs.getTimestamp("orderedAt")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // method ini untuk ambil list barang yg dibeli di 1 order
    public List<CartItem> getOrderItems(String idOrder) {
        List<CartItem> items = new ArrayList<>();
        String query = "SELECT p.idProduct, p.name, p.price, od.qty " +
                       "FROM OrderDetails od " +
                       "JOIN Products p ON od.idProduct = p.idProduct " +
                       "WHERE od.idOrder = '" + idOrder + "'";
        
        ResultSet rs = db.execQuery(query);
        
        try {
            while (rs.next()) {
                items.add(new CartItem(
                    rs.getString("idProduct"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("qty")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
    
    // method ini untuk admin ambil semua order dari semua customer
    public List<OrderHeader> getAllOrders() {
        List<OrderHeader> orders = new ArrayList<>();
        String query = "SELECT * FROM OrderHeaders";
        ResultSet rs = db.execQuery(query);
        
        try {
            while (rs.next()) {
                orders.add(new OrderHeader(
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
    
    // helper method buat cek stok produk
    private int getProductStock(String productId) {
        String query = "SELECT stock FROM Products WHERE idProduct = '" + productId + "'";
        ResultSet rs = db.execQuery(query);
        try {
            if (rs.next()) return rs.getInt("stock");
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }
}