package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Product;
import util.Connect;

public class ProductHandler {
    
    private Connect db = Connect.getInstance();

    // method ini untuk ambil semua data produk buat ditampilin di list
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM Products";
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
    
    // method ini untuk cari 1 produk spesifik berdasarkan id
    public Product getProduct(String idProduct) {
        String query = "SELECT * FROM Products WHERE idProduct = '" + idProduct + "'";
        ResultSet rs = db.execQuery(query);
        
        try {
            if (rs.next()) {
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
        return null;
    }

    // method ini untuk ambil produk yg stoknya masih tersedia (> 0)
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
    
    // method ini untuk update stok produk, sesuai spek di class diagram
    public String editProductStock(String idProduct, int newStock) {
        // validasi stok tidak boleh negatif
        if (newStock < 0) return "Stock cannot be negative";
        
        // update stok di database
        String query = String.format("UPDATE Products SET stock = %d WHERE idProduct = '%s'", newStock, idProduct);
        db.execUpdate(query);
        
        return "Success";
    }
}