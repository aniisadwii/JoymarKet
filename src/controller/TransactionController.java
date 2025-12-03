package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.CartItem;
import model.Customer;
import model.Promo;
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
    
 // Fitur Update Cart (Edit Qty)
    public String updateCartQty(String userId, String productId, int newQty) {
        // Cek stok dulu
        int currentStock = getProductStock(productId);
        if (newQty > currentStock) return "Stok tidak cukup! Sisa: " + currentStock;
        if (newQty <= 0) return "Jumlah harus lebih dari 0";

        String query = String.format("UPDATE CartItems SET count = %d WHERE idCustomer = '%s' AND idProduct = '%s'", 
                newQty, userId, productId);
        db.execUpdate(query);
        return "Success";
    }

    // Fitur Remove from Cart
    public void deleteCartItem(String userId, String productId) {
        String query = String.format("DELETE FROM CartItems WHERE idCustomer = '%s' AND idProduct = '%s'", 
                userId, productId);
        db.execUpdate(query);
    }
    
    public Promo getPromo(String code) {
        String query = "SELECT * FROM Promos WHERE code = '" + code + "'";
        ResultSet rs = db.execQuery(query);
        try {
            if (rs.next()) {
                return new Promo(
                    rs.getString("idPromo"),
                    rs.getString("code"),
                    rs.getDouble("discountPercentage")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Kalau gak ketemu
    }
    
 // UPDATE Method Checkout (Nambah Parameter promoCode)
    public String checkout(String userId, String promoCode) {
        List<CartItem> cart = getCartItems(userId);
        if (cart.isEmpty()) return "Cart is empty";

        // 1. Hitung Total Awal & Validasi Stok
        double totalAmount = 0;
        for (CartItem item : cart) {
            int currentStock = getProductStock(item.getIdProduct());
            if (currentStock < item.getQuantity()) {
                return "Stock changed! " + item.getProductName() + " not enough.";
            }
            totalAmount += item.getTotal();
        }

        // 2. Cek Promo Code (Kalau diisi)
        String idPromo = null; // Default null (kalo gak pake promo)
        
        if (promoCode != null && !promoCode.isEmpty()) {
            Promo promo = getPromo(promoCode);
            if (promo == null) {
                return "Invalid Promo Code!"; // Requirement: Must exist in DB
            }
            
            // Hitung Diskon
            double discount = totalAmount * (promo.getDiscountPercentage() / 100.0);
            totalAmount -= discount; // Kurangi total
            idPromo = promo.getIdPromo(); // Simpan ID Promo buat dimasukin ke DB
        }

        // 3. Cek Saldo User (Pake Total yg udah didiskon)
        Customer user = (Customer) Session.getInstance().getUser();
        if (user.getBalance() < totalAmount) {
            return "Balance not sufficient! (Total after discount: " + totalAmount + ")";
        }

        // --- MULAI TRANSAKSI ---
        String orderId = "OR" + System.currentTimeMillis();
        
        // Insert Header (Pake idPromo, bisa null)
        // Note: di SQL string '%s' kalo null bakal error string "null", jadi kita handle dikit stringnya
        String idPromoVal = (idPromo == null) ? "NULL" : "'" + idPromo + "'";
        
        String queryHeader = String.format("INSERT INTO OrderHeaders (idOrder, idCustomer, idPromo, totalAmount, status, orderedAt) VALUES ('%s', '%s', %s, %f, 'Pending', NOW())",
                orderId, userId, idPromoVal, totalAmount);
        
        db.execUpdate(queryHeader);

        // ... (Sisa logic insert detail, potong stok, potong saldo, clear cart SAMA KAYAK SEBELUMNYA) ...
        
        // Insert Detail
        for (CartItem item : cart) {
            String queryDetail = String.format("INSERT INTO OrderDetails VALUES ('%s', '%s', %d)", orderId, item.getIdProduct(), item.getQuantity());
            db.execUpdate(queryDetail);
            String updateStock = String.format("UPDATE Products SET stock = stock - %d WHERE idProduct = '%s'", item.getQuantity(), item.getIdProduct());
            db.execUpdate(updateStock);
        }

        // Potong Saldo & Clear Cart
        double newBalance = user.getBalance() - totalAmount;
        String updateBalance = String.format("UPDATE Customers SET balance = %f WHERE idCustomer = '%s'", newBalance, userId);
        db.execUpdate(updateBalance);
        user.setBalance(newBalance); // Update Session

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