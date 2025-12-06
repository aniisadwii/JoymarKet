package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Product;
import util.Connect;

public class ProductHandler {
    private Connect db = Connect.getInstance();

    // Ambil semua produk buat ditampilin di list
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
    
 // METHOD BARU: Sesuai Class Diagram "editProductStock"
    public String editProductStock(String idProduct, int newStock) {
        // Validasi Sederhana (Sesuai Activity Diagram)
        if (newStock < 0) return "Stock cannot be negative";
        
        // Update Database
        String query = String.format("UPDATE Products SET stock = %d WHERE idProduct = '%s'", newStock, idProduct);
        db.execUpdate(query);
        
        return "Success";
    }
}