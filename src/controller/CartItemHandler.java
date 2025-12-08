package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.CartItem;
import util.Connect;

public class CartItemHandler {
    
    // mengambil instance koneksi database
    private Connect db = Connect.getInstance();

    // method ini sesuai class diagram untuk mengambil daftar item di keranjang
    public List<CartItem> getCartItems(String idCustomer) {
        List<CartItem> cart = new ArrayList<>();
        // query join tabel cartitems dan products untuk mendapatkan detail produk
        String query = "SELECT c.idProduct, p.name, p.price, c.count " +
                       "FROM CartItems c " +
                       "JOIN Products p ON c.idProduct = p.idProduct " +
                       "WHERE c.idCustomer = '" + idCustomer + "'";
        
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

    // method ini sama persis dengan sequence diagram message 2: createCartItem
    // parameter idCustomer, idProduct, dan count sesuai dengan spesifikasi diagram
    public String createCartItem(String idCustomer, String idProduct, int count) {
        
        // validasi stok produk terlebih dahulu sebelum dimasukkan ke keranjang
        int currentStock = getProductStock(idProduct);
        
        // validasi jika produk tidak ditemukan atau stok habis
        if (currentStock == -1) return "Product not found";
        
        // validasi input jumlah tidak boleh nol atau negatif
        if (count <= 0) return "Quantity must be greater than 0";
        
        // validasi jumlah yang diminta tidak boleh melebihi stok tersedia
        if (count > currentStock) return "Stock not enough! (Remaining: " + currentStock + ")";

        // cek apakah produk sudah ada di keranjang user tersebut
        if (isProductInCart(idCustomer, idProduct)) {
            // jika sudah ada, lakukan update quantity (asumsi replace count sesuai logika umum)
            String query = String.format("UPDATE CartItems SET count = %d WHERE idCustomer = '%s' AND idProduct = '%s'", 
                    count, idCustomer, idProduct);
            db.execUpdate(query);
        } else {
            // jika belum ada, lakukan insert data baru ke tabel cartitems
            // ini sesuai dengan flow 'save data' pada activity diagram
            String query = String.format("INSERT INTO CartItems VALUES ('%s', '%s', %d)", 
                    idCustomer, idProduct, count);
            db.execUpdate(query);
        }
        
        // mengembalikan pesan sukses sesuai harapan sequence diagram
        return "Success";
    }

    // method ini sesuai dengan class diagram untuk fitur edit cart item
    public String editCartItem(String idCustomer, String idProduct, int count) {
        int currentStock = getProductStock(idProduct);
        
        // validasi stok saat update quantity
        if (count > currentStock) return "Stock not enough! (Remaining: " + currentStock + ")";
        if (count <= 0) return "Quantity must be greater than 0";

        String query = String.format("UPDATE CartItems SET count = %d WHERE idCustomer = '%s' AND idProduct = '%s'", 
                count, idCustomer, idProduct);
        db.execUpdate(query);
        return "Success";
    }

    // method ini sesuai dengan class diagram untuk fitur remove cart item
    public String deleteCartItem(String idCustomer, String idProduct) {
        try {
            String query = String.format("DELETE FROM CartItems WHERE idCustomer = '%s' AND idProduct = '%s'", 
                    idCustomer, idProduct);
            db.execUpdate(query);
            return "Success"; 
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed";
        }
    }

    // method helper internal untuk mengambil stok produk dari database
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

    // method helper internal untuk mengecek keberadaan produk di keranjang
    private boolean isProductInCart(String idCustomer, String idProduct) {
        String query = String.format("SELECT * FROM CartItems WHERE idCustomer = '%s' AND idProduct = '%s'", idCustomer, idProduct);
        ResultSet rs = db.execQuery(query);
        try {
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}