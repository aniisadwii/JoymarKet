package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Delivery;
import model.OrderHeader;
import util.Connect;

public class DeliveryHandler {
    
    private Connect db = Connect.getInstance();
    
 // === FITUR ASSIGN COURIER (ADMIN) ===

    // Method 1: Sesuai Sequence Diagram Message 1 (getOrderHeader)
    // Fungsinya mengambil detail 1 order spesifik.
    public OrderHeader getOrderHeader(String idOrder) {
        String query = "SELECT * FROM OrderHeaders WHERE idOrder = '" + idOrder + "'";
        ResultSet rs = db.execQuery(query);
        try {
            if (rs.next()) {
                return new OrderHeader(
                    rs.getString("idOrder"), 
                    rs.getString("idCustomer"), 
                    rs.getDouble("totalAmount"), 
                    rs.getString("status"), 
                    rs.getTimestamp("orderedAt")
                );
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    
 // Method 2: Sesuai Class Diagram (assignCourierToOrder)
    // Method ini mengimplementasikan Sequence Diagram Message 2 (createDelivery)
    public String assignCourierToOrder(String idOrder, String idCourier) {
        // Validasi sederhana (Activity Diagram: Validate Data)
        if (idCourier == null || idCourier.isEmpty()) return "Courier ID invalid";

        // 1. Simpan ke tabel Deliveries (Sequence Message 2.1.1: saveDA)
        String queryDelivery = String.format("INSERT INTO Deliveries (idOrder, idCourier, status) VALUES ('%s', '%s', 'Pending')", 
                idOrder, idCourier);
        db.execUpdate(queryDelivery);

        // 2. Update Status Order jadi In Progress (Kebutuhan Sistem)
        String queryUpdate = String.format("UPDATE OrderHeaders SET status = 'In Progress' WHERE idOrder = '%s'", idOrder);
        db.execUpdate(queryUpdate);

        return "Success";
    }

 // Method Helper buat View List (Activity Diagram: Fetch order list)
    public List<OrderHeader> getPendingOrders() {
        List<OrderHeader> orders = new ArrayList<>();
        String query = "SELECT * FROM OrderHeaders WHERE status = 'Pending'";
        ResultSet rs = db.execQuery(query);
        try {
            while (rs.next()) {
                orders.add(new OrderHeader(
                    rs.getString("idOrder"), 
                    rs.getString("idCustomer"), 
                    rs.getDouble("totalAmount"), 
                    rs.getString("status"), 
                    rs.getTimestamp("orderedAt")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return orders;
    }

 // Method Helper buat Dropdown
    public List<String> getCouriersList() {
        List<String> couriers = new ArrayList<>();
        String query = "SELECT idUser, fullName FROM Users WHERE role = 'Courier'";
        ResultSet rs = db.execQuery(query);
        try {
            while (rs.next()) { 
                couriers.add(rs.getString("idUser") + " - " + rs.getString("fullName")); 
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return couriers;
    }

    // === FITUR COURIER (TAMBAHAN BARU) ===
    
    // 1. Ambil Kerjaan Kurir (My Jobs)
    public List<OrderHeader> getDeliveries(String idCourier) {
        List<OrderHeader> jobs = new ArrayList<>();
        
        // Query tetap sama (JOIN)
        String query = "SELECT o.* FROM OrderHeaders o " +
                       "JOIN Deliveries d ON o.idOrder = d.idOrder " +
                       "WHERE d.idCourier = '" + idCourier + "' " +
                       "AND o.status IN ('In Progress', 'Pending', 'Delivered')"; 
        
        ResultSet rs = db.execQuery(query);
        try {
            while (rs.next()) {
                jobs.add(new OrderHeader(
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
    public String editDeliveryStatus(String idOrder, String idCourier, String newStatus) {
        try {
            // Validasi tambahan: Pastikan order ini beneran punya si Courier (Optional tapi bagus)
            // Tapi karena Class Diagram minta parameternya ada, kita masukin aja ke query biar aman.
            
            // 1. Update OrderHeaders
            String queryOrder = String.format("UPDATE OrderHeaders SET status = '%s' WHERE idOrder = '%s'", newStatus, idOrder);
            db.execUpdate(queryOrder);
            
            // 2. Update Deliveries (Pakai idCourier di WHERE clause biar sesuai parameter)
            String queryDelivery = String.format("UPDATE Deliveries SET status = '%s' WHERE idOrder = '%s' AND idCourier = '%s'", 
                    newStatus, idOrder, idCourier);
            db.execUpdate(queryDelivery);
            
            return "Success";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed";
        }
    }
    
 // 1. getAllDeliveries() -> Sesuai Class Diagram
    // Return: List of Delivery Objects
    public List<Delivery> getAllDeliveries() {
        List<Delivery> deliveries = new ArrayList<>();
        String query = "SELECT * FROM Deliveries";
        ResultSet rs = db.execQuery(query);
        
        try {
            while (rs.next()) {
                deliveries.add(new Delivery(
                    rs.getString("idOrder"),
                    rs.getString("idCourier"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deliveries;
    }

    // 2. getDelivery(idOrder, idCourier) -> Sesuai Class Diagram
    // Return: Single Delivery Object
    public Delivery getDelivery(String idOrder, String idCourier) {
        String query = String.format("SELECT * FROM Deliveries WHERE idOrder = '%s' AND idCourier = '%s'", idOrder, idCourier);
        ResultSet rs = db.execQuery(query);
        
        try {
            if (rs.next()) {
                return new Delivery(
                    rs.getString("idOrder"),
                    rs.getString("idCourier"),
                    rs.getString("status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}