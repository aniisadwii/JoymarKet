package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Order;
import util.Connect;

public class DeliveryHandler {
    
    private Connect db = Connect.getInstance();

    // === FITUR ADMIN (Yang kemarin udah dibuat) ===
    public List<Order> getPendingOrders() {
        // ... (Kodingan lama getPendingOrders) ...
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM OrderHeaders WHERE status = 'Pending'";
        ResultSet rs = db.execQuery(query);
        try {
            while (rs.next()) {
                orders.add(new Order(rs.getString("idOrder"), rs.getString("idCustomer"), rs.getDouble("totalAmount"), rs.getString("status"), rs.getTimestamp("orderedAt")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return orders;
    }

    public List<String> getCouriersList() {
        // ... (Kodingan lama getCouriersList) ...
        List<String> couriers = new ArrayList<>();
        String query = "SELECT idUser, fullName FROM Users WHERE role = 'Courier'";
        ResultSet rs = db.execQuery(query);
        try {
            while (rs.next()) { couriers.add(rs.getString("idUser") + " - " + rs.getString("fullName")); }
        } catch (SQLException e) { e.printStackTrace(); }
        return couriers;
    }

    public String assignCourierToOrder(String idOrder, String idCourier) {
        // Validasi null doang, gak perlu split string lagi
        if (idCourier == null || idCourier.isEmpty()) return "Courier ID invalid";

        // Query Insert Deliveries
        String queryDelivery = String.format("INSERT INTO Deliveries (idOrder, idCourier, status) VALUES ('%s', '%s', 'Pending')", 
                idOrder, idCourier);
        db.execUpdate(queryDelivery);

        // Update Status Order
        String queryUpdate = String.format("UPDATE OrderHeaders SET status = 'In Progress' WHERE idOrder = '%s'", idOrder);
        db.execUpdate(queryUpdate);

        return "Success";
    }

    // === FITUR COURIER (TAMBAHAN BARU) ===
    
    // 1. Ambil Kerjaan Kurir (My Jobs)
    public List<Order> getDeliveries(String idCourier) {
        List<Order> jobs = new ArrayList<>();
        
        // Query tetap sama (JOIN)
        String query = "SELECT o.* FROM OrderHeaders o " +
                       "JOIN Deliveries d ON o.idOrder = d.idOrder " +
                       "WHERE d.idCourier = '" + idCourier + "' " +
                       "AND o.status IN ('In Progress', 'Pending', 'Delivered')"; 
        
        ResultSet rs = db.execQuery(query);
        try {
            while (rs.next()) {
                jobs.add(new Order(
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
        return jobs;
    }
    
    // 2. Update Status Pengiriman (Sesuai Class Diagram: editDeliveryStatus)
    public String editDeliveryStatus(String idOrder, String newStatus) {
        try {
            // Update di OrderHeaders
            String queryOrder = String.format("UPDATE OrderHeaders SET status = '%s' WHERE idOrder = '%s'", newStatus, idOrder);
            db.execUpdate(queryOrder);
            
            // Update di Deliveries juga biar sinkron
            String queryDelivery = String.format("UPDATE Deliveries SET status = '%s' WHERE idOrder = '%s'", newStatus, idOrder);
            db.execUpdate(queryDelivery);
            
            return "Success"; // Lapor Sukses
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed"; // Lapor Gagal
        }
    }
}