package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.CartItem;
import model.Customer;
import util.Connect;
import util.Session;

public class TransactionController {
    
    private Connect db = Connect.getInstance();

    public String topUp(String amountStr) {
        // 1. Validasi: Harus Diisi
        if (amountStr.isEmpty()) return "Amount must be filled";

        // 2. Validasi: Harus Angka
        if (!isNumeric(amountStr)) return "Amount must be numeric";

        double amount = Double.parseDouble(amountStr);

        // 3. Validasi: Minimal 10.000
        if (amount < 10000) return "Minimum top up is Rp 10.000";

        // Kalau lolos, update Database
        Customer currentUser = (Customer) Session.getInstance().getUser();
        String userId = currentUser.getIdUser();
        double newBalance = currentUser.getBalance() + amount;

        // Query Update
        String query = String.format("UPDATE Customers SET balance = %f WHERE idCustomer = '%s'", newBalance, userId);
        db.execUpdate(query);

        // PENTING: Update juga data di Session biar Real-time berubah di layar
        currentUser.setBalance(newBalance);
        
        return "Success";
    }
    
 // Method baru: Add To Cart
    public String addToCart(String userId, String productId, int quantity) {
        // 1. Cek Stok di Database dulu
        int currentStock = getProductStock(productId);
        
        if (currentStock == -1) return "Product not found";
        if (quantity <= 0) return "Quantity must be greater than 0";
        if (quantity > currentStock) return "Stock not enough! (Remaining: " + currentStock + ")";

        // 2. Cek apakah barang udah ada di cart user ini?
        if (isProductInCart(userId, productId)) {
            // Update quantity (nambahin yang lama)
            // Note: Simplifikasi, kita replace aja atau tambah logic fetch qty lama + qty baru
            // Di sini aku buat logic: Update jadi qty baru (simplifikasi biar cepet)
            String query = String.format("UPDATE CartItems SET count = %d WHERE idCustomer = '%s' AND idProduct = '%s'", 
                    quantity, userId, productId);
            db.execUpdate(query);
        } else {
            // Insert baru
            String query = String.format("INSERT INTO CartItems VALUES ('%s', '%s', %d)", 
                    userId, productId, quantity);
            db.execUpdate(query);
        }

        return "Success";
    }

    // Helper: Ambil stok produk
    private int getProductStock(String productId) {
        String query = "SELECT stock FROM Products WHERE idProduct = '" + productId + "'";
        ResultSet rs = db.execQuery(query);
        try {
            if (rs.next()) return rs.getInt("stock");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Helper: Cek barang di cart
    private boolean isProductInCart(String userId, String productId) {
        String query = String.format("SELECT * FROM CartItems WHERE idCustomer = '%s' AND idProduct = '%s'", userId, productId);
        ResultSet rs = db.execQuery(query);
        try {
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<CartItem> getCartItems(String userId) {
        List<CartItem> cart = new ArrayList<>();
        String query = "SELECT c.idProduct, p.name, p.price, c.count " +
                       "FROM CartItems c " +
                       "JOIN Products p ON c.idProduct = p.idProduct " +
                       "WHERE c.idCustomer = '" + userId + "'";
        
        ResultSet rs = db.execQuery(query);
        try {
            while (rs.next()) {
                cart.add(new CartItem(
                    rs.getString("idProduct"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("count")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cart;
    }

    // 2. Logic Checkout (Sesuai Soal)
    public String checkout(String userId) {
        // A. Hitung Total & Validasi Stok lagi (Takutnya keduluan orang lain)
        List<CartItem> cart = getCartItems(userId);
        if (cart.isEmpty()) return "Cart is empty";

        double totalAmount = 0;
        
        // Cek stok & hitung total
        for (CartItem item : cart) {
            int currentStock = getProductStock(item.getIdProduct());
            if (currentStock < item.getQuantity()) {
                return "Stock changed! " + item.getProductName() + " not enough.";
            }
            totalAmount += item.getTotal();
        }

        // B. Cek Saldo User
        Customer user = (Customer) Session.getInstance().getUser();
        if (user.getBalance() < totalAmount) {
            return "Balance not sufficient! (Total: " + totalAmount + ")";
        }

        // --- MULAI TRANSAKSI ---
        
        // 1. Generate Order ID (OR + Timestamp)
        String orderId = "OR" + System.currentTimeMillis();
        
        // 2. Insert ke OrderHeaders (Status Default: Pending)
        String queryHeader = String.format("INSERT INTO OrderHeaders (idOrder, idCustomer, totalAmount, status, orderedAt) VALUES ('%s', '%s', %f, 'Pending', NOW())",
                orderId, userId, totalAmount);
        db.execUpdate(queryHeader);

        // 3. Pindahin Item ke OrderDetails & Kurangi Stok
        for (CartItem item : cart) {
            // Insert Detail
            String queryDetail = String.format("INSERT INTO OrderDetails VALUES ('%s', '%s', %d)",
                    orderId, item.getIdProduct(), item.getQuantity());
            db.execUpdate(queryDetail);

            // Kurangi Stok Produk
            String updateStock = String.format("UPDATE Products SET stock = stock - %d WHERE idProduct = '%s'",
                    item.getQuantity(), item.getIdProduct());
            db.execUpdate(updateStock);
        }

        // 4. Potong Saldo User
        double newBalance = user.getBalance() - totalAmount;
        String updateBalance = String.format("UPDATE Customers SET balance = %f WHERE idCustomer = '%s'",
                newBalance, userId);
        db.execUpdate(updateBalance);
        
        // Update Session biar saldo di layar berubah realtime
        user.setBalance(newBalance);

        // 5. Kosongin Keranjang
        String clearCart = "DELETE FROM CartItems WHERE idCustomer = '" + userId + "'";
        db.execUpdate(clearCart);

        return "Success";
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}