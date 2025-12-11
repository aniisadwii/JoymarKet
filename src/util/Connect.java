package util;

import java.sql.*;

public class Connect {
    private final String USERNAME = "root"; 
    private final String PASSWORD = ""; 
    private final String DATABASE = "joymarket2"; 
    private final String HOST = "localhost:3306";
    private final String CONNECTION = String.format("jdbc:mysql://%s/%s", HOST, DATABASE);

    private Connection con;
    private Statement st;
    private static Connect instance;

    // method ini untuk mendapatkan instance tunggal koneksi database (singleton pattern)
    public static Connect getInstance() {
        if (instance == null) {
            instance = new Connect();
        }
        return instance;
    }

    // constructor privat ini untuk inisialisasi driver jdbc dan membangun koneksi ke database mysql
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

    // method ini untuk eksekusi query pengambilan data (select) dan mengembalikan result set
    public ResultSet execQuery(String query) {
        try {
            st = con.createStatement();
            return st.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // method ini untuk eksekusi query manipulasi data (insert, update, delete)
    public void execUpdate(String query) {
        try {
            st = con.createStatement();
            st.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // method ini untuk mengambil objek koneksi mentah jika diperlukan untuk prepared statement
    public Connection getConnection() {
        return con;
    }
}