package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import model.User;
import util.Connect;
import util.Session;
import model.Customer;
import model.Admin;
import model.Courier;

public class UserController {
    
    private Connect db = Connect.getInstance();

    // Validasi Login
 // Ganti return type dari boolean jadi User (atau void kalau mau langsung set session)
    public User login(String email, String password) {
        User user = null;
        String query = String.format("SELECT * FROM Users WHERE email = '%s' AND password = '%s'", email, password);
        ResultSet rs = db.execQuery(query);

        try {
            if (rs.next()) {
                String id = rs.getString("idUser");
                String name = rs.getString("fullName");
                String phone = rs.getString("phone");
                String addr = rs.getString("address");
                String role = rs.getString("role");
                
                // Karena class User itu Abstract, kita harus instansiasi anaknya (Polymorphism)
                if (role.equals("Customer")) {
                    // Default balance 0 dulu, nanti bisa di-fetch lagi kalau perlu
                    user = new Customer(id, name, email, password, phone, addr, 0.0);
                } else if (role.equals("Admin")) {
                    // Default emergency contact null dulu
                    user = new Admin(id, name, email, password, phone, addr, "-");
                } else if (role.equals("Courier")) {
                    // Karena di tabel 'Users' gak ada info kendaraan, kita isi dummy dulu "Unknown"
                    // Nanti kalau dashboardnya udah canggih, baru kita fetch dari tabel 'Couriers'
                    user = new Courier(id, name, email, password, phone, addr, "Unknown", "Unknown");
                }
                
                // Simpan ke Session biar bisa dipake di halaman lain
                Session.getInstance().setUser(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return user; // Kalau gagal login, ini bakal null
    }

    // Validasi Register Customer (Sesuai Soal)
    // Return String kosong kalau sukses, return error message kalau gagal
    public String registerCustomer(String fullName, String email, String password, String confirmPass, String phone, String address, String gender) {
        // 1. Full Name: Cannot be empty
        if (fullName.isEmpty()) return "Full name cannot be empty";

        // 2. Email: Must end with @gmail.com & Unique
        if (email.isEmpty()) return "Email must be filled";
        if (!email.endsWith("@gmail.com")) return "Email must end with @gmail.com";
        if (!isEmailUnique(email)) return "Email already registered";

        // 3. Password: Min 6 chars
        if (password.length() < 6) return "Password must be at least 6 characters";
        if (!password.equals(confirmPass)) return "Password must match confirmation";

        // 4. Phone: Numeric, 10-13 digits
        if (phone.isEmpty()) return "Phone must be filled";
        if (phone.length() < 10 || phone.length() > 13) return "Phone must be 10-13 digits";
        if (!isNumeric(phone)) return "Phone must be numeric"; // Kita bikin helper function manual

        // 5. Address: Must be filled
        if (address.isEmpty()) return "Address must be filled";
        
        // Note: Gender dihilangkan sesuai revisi Aslab, jadi hiraukan parameter gender di atas atau hapus aja nanti.
        
        // Kalau lolos semua validasi, masukin ke DB
        // Generate ID (Misal CU + random angka atau logic lain)
        String newId = generateId();
        
        // Insert ke Table Users
        String queryUser = String.format("INSERT INTO Users VALUES ('%s', '%s', '%s', '%s', '%s', '%s', 'Customer')", 
                newId, fullName, email, password, phone, address);
        
        // Insert ke Table Customers
        String queryCustomer = String.format("INSERT INTO Customers VALUES ('%s', 0)", newId);

        db.execUpdate(queryUser);
        db.execUpdate(queryCustomer);

        return "Success";
    }

    private String generateId() {
        // Ambil ID Customer terakhir yang diurutkan dari terbesar
        // Kita filter cuma yang depannya 'CU' biar aman
        String query = "SELECT idUser FROM Users WHERE role = 'Customer' ORDER BY idUser DESC LIMIT 1";
        ResultSet rs = db.execQuery(query);
        
        try {
            if (rs.next()) {
                String lastId = rs.getString("idUser");
                // lastId contohnya "CU001"
                
                // Ambil angkanya aja (substring dari index 2 sampai habis) -> "001"
                String numberStr = lastId.substring(2);
                
                // Ubah jadi integer -> 1
                int number = Integer.parseInt(numberStr);
                
                // Tambah 1 -> 2
                number++;
                
                // Format balik jadi string 3 digit (pake %03d) -> "CU002"
                return String.format("CU%03d", number);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Kalau database masih kosong atau belum ada customer, mulai dari CU001
        return "CU001";
    }

	// Helper: Cek Email Unik
    private boolean isEmailUnique(String email) {
        String query = "SELECT * FROM Users WHERE email = '" + email + "'";
        ResultSet rs = db.execQuery(query);
        try {
            return !rs.next(); // Kalau gak ada data, berarti unik (true)
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper: Cek Numeric tanpa Regex (Manual loop)
    private boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }
}