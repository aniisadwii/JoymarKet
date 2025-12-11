package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Courier;
import util.Connect;

public class CourierHandler {
    
    private Connect db = Connect.getInstance();
    
    // method ini untuk ambil semua kurir beserta info kendaraannya
    public List<Courier> getAllCouriers() {
        List<Courier> couriers = new ArrayList<>();
        
        // join tabel users dan couriers
        String query = "SELECT u.idUser, u.fullName, u.email, u.password, u.phone, u.address, " +
                       "c.vehicleType, c.vehiclePlate " +
                       "FROM Users u " +
                       "JOIN Couriers c ON u.idUser = c.idCourier " + 
                       "WHERE u.role = 'Courier'";
        
        ResultSet rs = db.execQuery(query);
        
        try {
            while (rs.next()) {
                couriers.add(new Courier(
                    rs.getString("idUser"),
                    rs.getString("fullName"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("vehicleType"),
                    rs.getString("vehiclePlate")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return couriers;
    }
    
    // method ini untuk ambil data kurir spesifik berdasarkan id
    public Courier getCourier(String idCourier) {
        String query = "SELECT u.idUser, u.fullName, u.email, u.password, u.phone, u.address, " +
                       "c.vehicleType, c.vehiclePlate " +
                       "FROM Users u " +
                       "JOIN Couriers c ON u.idUser = c.idCourier " + 
                       "WHERE u.idUser = '" + idCourier + "'";
        
        ResultSet rs = db.execQuery(query);
        
        try {
            if (rs.next()) {
                return new Courier(
                    rs.getString("idUser"),
                    rs.getString("fullName"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("vehicleType"),
                    rs.getString("vehiclePlate")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}