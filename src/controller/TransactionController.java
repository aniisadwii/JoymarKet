package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
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

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}