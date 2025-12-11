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
    
    // === FITUR ADMIN ===

    // method ini untuk ambil detail order berdasarkan id
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
    
    // method ini untuk admin assign kurir ke order tertentu
    public String assignCourierToOrder(String idOrder, String idCourier) {
        if (idCourier == null || idCourier.isEmpty()) return "Courier ID invalid";

        // 1. insert data delivery baru
        String queryDelivery = String.format("INSERT INTO Deliveries (idOrder, idCourier, status) VALUES ('%s', '%s', 'Pending')", 
                idOrder, idCourier);
        db.execUpdate(queryDelivery);

        // 2. update status order jadi 'in progress'
        String queryUpdate = String.format("UPDATE OrderHeaders SET status = 'In Progress' WHERE idOrder = '%s'", idOrder);
        db.execUpdate(queryUpdate);

        return "Success";
    }

    // method ini untuk ambil list order yg statusnya masih 'pending'
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

    // method ini untuk ambil list nama kurir buat dropdown
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

    // === FITUR COURIER ===
    
    // method ini untuk ambil list kerjaan yg ditugaskan ke kurir tertentu
    public List<OrderHeader> getDeliveries(String idCourier) {
        List<OrderHeader> jobs = new ArrayList<>();
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
    
    // method ini untuk kurir update status pengiriman
    public String editDeliveryStatus(String idOrder, String idCourier, String newStatus) {
        try {
            // update status di order header
            String queryOrder = String.format("UPDATE OrderHeaders SET status = '%s' WHERE idOrder = '%s'", newStatus, idOrder);
            db.execUpdate(queryOrder);
            
            // update status di tabel delivery juga
            String queryDelivery = String.format("UPDATE Deliveries SET status = '%s' WHERE idOrder = '%s' AND idCourier = '%s'", 
                    newStatus, idOrder, idCourier);
            db.execUpdate(queryDelivery);
            
            return "Success";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed";
        }
    }
    
    // method ini untuk ambil semua data delivery (syarat diagram)
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

    // method ini untuk ambil data delivery spesifik (syarat diagram)
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