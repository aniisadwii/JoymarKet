package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Order;
import model.User; // Pake User atau bikin model Courier khusus jg boleh
import util.Connect;

public class AdminController {
    
    private Connect db = Connect.getInstance();

    // 1. Ambil Orderan yang Pending
    public List<Order> getPendingOrders() {
        List<Order> orders = new ArrayList<>();
        // Kita cuma peduli sama yang belum diurus (Pending)
        String query = "SELECT * FROM OrderHeaders WHERE status = 'Pending'";
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

    // 2. Ambil List Kurir buat Dropdown
    public List<String> getCouriersList() {
        List<String> couriers = new ArrayList<>();
        String query = "SELECT idUser, fullName FROM Users WHERE role = 'Courier'";
        ResultSet rs = db.execQuery(query);
        try {
            while (rs.next()) {
                // Format: "CO001 - Mas Kurir"
                couriers.add(rs.getString("idUser") + " - " + rs.getString("fullName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return couriers;
    }

    // 3. Assign Order (Inti Fitur)
    public String assignToCourier(String idOrder, String idCourierWithName) {
        if (idCourierWithName == null) return "Please select a courier";
        
        // Pecah string "CO001 - Mas Kurir" ambil ID-nya aja "CO001"
        String idCourier = idCourierWithName.split(" - ")[0];

        // A. Insert ke tabel Deliveries
        String queryDelivery = String.format("INSERT INTO Deliveries (idOrder, idCourier, status) VALUES ('%s', '%s', 'Pending')", 
                idOrder, idCourier);
        db.execUpdate(queryDelivery);

        // B. Update status di OrderHeaders jadi "In Progress" (atau status lain biar gak Pending lagi)
        String queryUpdate = String.format("UPDATE OrderHeaders SET status = 'In Progress' WHERE idOrder = '%s'", idOrder);
        db.execUpdate(queryUpdate);

        return "Success";
    }
    
 // 4. Ambil Semua Produk (Buat Tab Manage Stock)
    public List<model.Product> getAllProducts() {
        List<model.Product> products = new ArrayList<>();
        String query = "SELECT * FROM Products";
        ResultSet rs = db.execQuery(query);
        try {
            while (rs.next()) {
                products.add(new model.Product(
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

    // 5. Update Stock Produk
    public String updateStock(String idProduct, int newStock) {
        if (newStock < 0) return "Stock cannot be negative";
        
        String query = String.format("UPDATE Products SET stock = %d WHERE idProduct = '%s'", newStock, idProduct);
        db.execUpdate(query);
        
        return "Success";
    }
}