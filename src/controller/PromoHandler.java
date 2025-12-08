package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Promo;
import util.Connect;

public class PromoHandler {
    
    private Connect db = Connect.getInstance();

    // Method 1: getAllPromos() -> Sesuai Diagram
    // (Meskipun mungkin belum kepake di fitur sekarang, tapi wajib ada buat syarat diagram)
    public List<Promo> getAllPromos() {
        List<Promo> promos = new ArrayList<>();
        String query = "SELECT * FROM Promos";
        ResultSet rs = db.execQuery(query);
        
        try {
            while (rs.next()) {
                promos.add(new Promo(
                    rs.getString("idPromo"),
                    rs.getString("code"),
                    rs.getDouble("discountPercentage")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return promos;
    }

    // Method 2: getPromo(code) -> Sesuai Diagram & Dipakai di Checkout
    public Promo getPromo(String code) {
        String query = "SELECT * FROM Promos WHERE code = '" + code + "'";
        ResultSet rs = db.execQuery(query);
        
        try {
            if (rs.next()) {
                return new Promo(
                    rs.getString("idPromo"),
                    rs.getString("code"),
                    rs.getDouble("discountPercentage")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; 
    }
}