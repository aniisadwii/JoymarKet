package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Courier;
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
    
 // Return: Object Courier (Single)
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