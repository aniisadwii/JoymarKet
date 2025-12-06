package util;

import java.sql.*;

public class Connect {
    private final String USERNAME = "root"; // Sesuaikan sama user XAMPP kamu
    private final String PASSWORD = "";     // Biasanya kosong kalo default
    private final String DATABASE = "joymarket2"; // Sesuai nama DB kamu
    private final String HOST = "localhost:3306";
    private final String CONNECTION = String.format("jdbc:mysql://%s/%s", HOST, DATABASE);

    private Connection con;
    private Statement st;
    private static Connect instance;

    // Singleton Pattern: Biar koneksinya cuma dibuat sekali
    public static Connect getInstance() {
        if (instance == null) {
            instance = new Connect();
        }
        return instance;
    }

    private Connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(CONNECTION, USERNAME, PASSWORD);
            st = con.createStatement();
            System.out.println("Database connected successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to connect to the database.");
        }
    }

    // Buat jalanin query SELECT (ngambil data)
    public ResultSet execQuery(String query) {
        try {
            st = con.createStatement();
            return st.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Buat jalanin query INSERT, UPDATE, DELETE
    public void execUpdate(String query) {
        try {
            st = con.createStatement();
            st.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Jangan lupa siapin PreparedStatement juga nanti buat security!
    public Connection getConnection() {
        return con;
    }
}