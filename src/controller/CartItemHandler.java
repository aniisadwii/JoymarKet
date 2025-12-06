package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.CartItem;
import util.Connect;

public class CartItemHandler {
    
    private Connect db = Connect.getInstance();

    // Mengambil semua item di keranjang
    public List<CartItem> getCartItems(String idCustomer) {
        List<CartItem> cart = new ArrayList<>();
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

    // Method baru sesuai Diagram: createCartItem (dulu addToCart)
    public String createCartItem(String idCustomer, String idProduct, int count) {
        // Cek Stok dulu
        int currentStock = getProductStock(idProduct);
        if (currentStock == -1) return "Product not found";
        if (count <= 0) return "Quantity must be greater than 0";
        if (count > currentStock) return "Stock not enough! (Remaining: " + currentStock + ")";

        if (isProductInCart(idCustomer, idProduct)) {
            // Update kalau udah ada
            String query = String.format("UPDATE CartItems SET count = %d WHERE idCustomer = '%s' AND idProduct = '%s'", 
                    count, idCustomer, idProduct); // Asumsi replace qty
            db.execUpdate(query);
        } else {
            // Insert baru
            String query = String.format("INSERT INTO CartItems VALUES ('%s', '%s', %d)", 
                    idCustomer, idProduct, count);
            db.execUpdate(query);
        }
        return "Success";
    }

    // Method baru sesuai Diagram: editCartItem (dulu updateCartQty)
    public String editCartItem(String idCustomer, String idProduct, int count) {
        int currentStock = getProductStock(idProduct);
        if (count > currentStock) return "Stock not enough! (Remaining: " + currentStock + ")";
        if (count <= 0) return "Quantity must be greater than 0";

        String query = String.format("UPDATE CartItems SET count = %d WHERE idCustomer = '%s' AND idProduct = '%s'", 
                count, idCustomer, idProduct);
        db.execUpdate(query);
        return "Success";
    }

    // Delete item
    public String deleteCartItem(String idCustomer, String idProduct) {
        try {
            String query = String.format("DELETE FROM CartItems WHERE idCustomer = '%s' AND idProduct = '%s'", 
                    idCustomer, idProduct);
            db.execUpdate(query);
            return "Success"; // Balikin status sukses
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed";
        }
    }

    // Helper Private
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