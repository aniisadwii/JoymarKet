package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Courier;
import model.Order;
import util.Connect;

public class CourierHandler {
    
    private Connect db = Connect.getInstance();
    
 // Method getAllCouriers sesuai Class Diagram
    public List<Courier> getAllCouriers() {
        List<Courier> couriers = new ArrayList<>();
        
        // QUERY JOIN: Gabungkan data dari 'Users' dan 'Couriers'
        // Biar kita bisa isi constructor Courier yang lengkap tadi
        String query = "SELECT u.idUser, u.fullName, u.email, u.password, u.phone, u.address, " +
                       "c.vehicleType, c.vehiclePlate " +
                       "FROM Users u " +
                       "JOIN Couriers c ON u.idUser = c.idCourier " + // Kunci JOIN-nya di sini
                       "WHERE u.role = 'Courier'";
        
        ResultSet rs = db.execQuery(query);
        
        try {
            while (rs.next()) {
                // Di sini kita bikin objek Courier UTUH sesuai Diagram
                couriers.add(new Courier(
                    rs.getString("idUser"),      // Dari tabel Users
                    rs.getString("fullName"),    // Dari tabel Users
                    rs.getString("email"),       // Dari tabel Users
                    rs.getString("password"),    // Dari tabel Users
                    rs.getString("phone"),       // Dari tabel Users
                    rs.getString("address"),     // Dari tabel Users
                    rs.getString("vehicleType"), // Dari tabel Couriers
                    rs.getString("vehiclePlate") // Dari tabel Couriers
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return couriers;
    }

    // 1. Ambil Kerjaan Kurir (Order yang di-assign ke dia)
    public List<Order> getMyJobs(String courierId) {
        List<Order> jobs = new ArrayList<>();
        
        // Kita JOIN antara Deliveries & OrderHeaders
        // Biar tau Order mana aja yang harus diantar si courierId
        String query = "SELECT o.* FROM OrderHeaders o " +
                       "JOIN Deliveries d ON o.idOrder = d.idOrder " +
                       "WHERE d.idCourier = '" + courierId + "' " +
                       "AND o.status = 'In Progress'"; // Kita tampilin yang lagi jalan aja
        
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
    
 // 2. Update Status (Bisa Pending, In Progress, atau Delivered)
    public void updateStatus(String orderId, String newStatus) {
        // Update di OrderHeaders
        String queryOrder = String.format("UPDATE OrderHeaders SET status = '%s' WHERE idOrder = '%s'", newStatus, orderId);
        db.execUpdate(queryOrder);
        
        // Update di Deliveries juga biar sinkron
        String queryDelivery = String.format("UPDATE Deliveries SET status = '%s' WHERE idOrder = '%s'", newStatus, orderId);
        db.execUpdate(queryDelivery);
    }

//    // 2. Update Status Jadi Delivered
//    public String completeDelivery(String orderId) {
//        // Update di OrderHeaders
//        String queryOrder = String.format("UPDATE OrderHeaders SET status = 'Delivered' WHERE idOrder = '%s'", orderId);
//        db.execUpdate(queryOrder);
//        
//        // Update di Deliveries juga biar sinkron
//        String queryDelivery = String.format("UPDATE Deliveries SET status = 'Delivered' WHERE idOrder = '%s'", orderId);
//        db.execUpdate(queryDelivery);
//
//        return "Success";
//    }
}