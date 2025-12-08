package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Product;
import util.Connect;

public class ProductHandler {
    
    // ambil koneksi ke database
    private Connect db = Connect.getInstance();

    // method ini buat ambil semua produk buat ditampilin di list view
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM Products";
        ResultSet rs = db.execQuery(query);
        
        try {
            while (rs.next()) {
                // bungkus data dari database jadi objek product
                products.add(new Product(
                    rs.getString("idProduct"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    rs.getString("category")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    
    // fungsinya ambil satu produk spesifik berdasarkan id
    public Product getProduct(String idProduct) {
        String query = "SELECT * FROM Products WHERE idProduct = '" + idProduct + "'";
        ResultSet rs = db.execQuery(query);
        
        try {
            if (rs.next()) {
                // balikin satu objek product kalau ketemu
                return new Product(
                    rs.getString("idProduct"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    rs.getString("category")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // balikin null kalau produk ga ada
    }

    // asumsinya kita cuma ambil produk yang stoknya masih ada (lebih dari 0)
    public List<Product> getAvailableProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM Products WHERE stock > 0";
        ResultSet rs = db.execQuery(query);
        
        try {
            while (rs.next()) {
                products.add(new Product(
                    rs.getString("idProduct"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    rs.getString("category")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    
    // method buat update stok, sesuai spek di class diagram
    public String editProductStock(String idProduct, int newStock) {
        // validasi sederhana biar stok ga minus, sesuai logika activity diagram
        if (newStock < 0) return "Stock cannot be negative";
        
        // jalanin update ke tabel products
        String query = String.format("UPDATE Products SET stock = %d WHERE idProduct = '%s'", newStock, idProduct);
        db.execUpdate(query);
        
        return "Success";
    }
}