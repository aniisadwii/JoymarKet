package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Order;
import util.Connect;

public class CourierController {
    
    private Connect db = Connect.getInstance();

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

    // 2. Update Status Jadi Delivered
    public String completeDelivery(String orderId) {
        // Update di OrderHeaders
        String queryOrder = String.format("UPDATE OrderHeaders SET status = 'Delivered' WHERE idOrder = '%s'", orderId);
        db.execUpdate(queryOrder);
        
        // Update di Deliveries juga biar sinkron
        String queryDelivery = String.format("UPDATE Deliveries SET status = 'Delivered' WHERE idOrder = '%s'", orderId);
        db.execUpdate(queryDelivery);

        return "Success";
    }
}