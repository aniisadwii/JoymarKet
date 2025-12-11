package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import model.User;
import util.Connect;
import util.Session;
import model.Customer;
import model.Admin;
import model.Courier;

public class UserHandler {
    
    // ambil koneksi database singleton
    private Connect db = Connect.getInstance();

    // method ini untuk proses registrasi user baru
    // validasi password match udah dilakukan di view, jadi disini tinggal simpan
    public User saveDataUser(String fullName, String email, String password, String phone, String address) throws Exception {
        
        // validasi nama tidak boleh kosong
        if (fullName.isEmpty()) throw new Exception("Full name cannot be empty");
        
        // validasi email wajib diisi, format gmail, dan belum terdaftar
        if (email.isEmpty()) throw new Exception("Email must be filled");
        if (!email.endsWith("@gmail.com")) throw new Exception("Email must end with @gmail.com");
        if (!isEmailUnique(email)) throw new Exception("Email already registered");

        // validasi password minimal 6 karakter
        if (password.length() < 6) throw new Exception("Password must be at least 6 characters");
        
        // validasi no hp harus angka dan panjang 10-13 digit
        if (phone.isEmpty()) throw new Exception("Phone must be filled");
        if (phone.length() < 10 || phone.length() > 13) throw new Exception("Phone must be 10-13 digits");
        if (!isNumeric(phone)) throw new Exception("Phone must be numeric");

        // validasi alamat wajib diisi
        if (address.isEmpty()) throw new Exception("Address must be filled");

        // generate id customer baru secara otomatis
        String newId = generateId(); 
        
        // query insert ke tabel users
        String queryUser = String.format("INSERT INTO Users VALUES ('%s', '%s', '%s', '%s', '%s', '%s', 'Customer')", 
                newId, fullName, email, password, phone, address);
        
        // query insert ke tabel customers dgn saldo awal 0
        String queryCustomer = String.format("INSERT INTO Customers VALUES ('%s', 0)", newId);

        try {
            // eksekusi simpan data ke database
            db.execUpdate(queryUser);
            db.execUpdate(queryCustomer);
            
            // return object customer baru sesuai return value di sequence diagram
            return new Customer(newId, fullName, email, password, phone, address, 0.0);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Database Error: Gagal menyimpan data."); 
        }
    }

    // method ini untuk cek login user, return object user kalo sukses
    public User login(String email, String password) {
        
        // validasi input tidak boleh kosong
        if (email.isEmpty() || password.isEmpty()) {
            return null; 
        }

        User user = null;
        
        // cari user yg email & password-nya cocok
        String query = String.format("SELECT * FROM Users WHERE email = '%s' AND password = '%s'", email, password);
        ResultSet rs = db.execQuery(query);

        try {
            if (rs.next()) {
                // ambil data user dari result set
                String id = rs.getString("idUser");
                String name = rs.getString("fullName");
                String phone = rs.getString("phone");
                String addr = rs.getString("address");
                String role = rs.getString("role");
                
                // inisialisasi object user sesuai role masing-masing
                if (role.equals("Customer")) {
                    // khusus customer ambil saldo dari tabel customers
                    double balance = 0.0;
                    String queryBalance = "SELECT balance FROM Customers WHERE idCustomer = '" + id + "'";
                    ResultSet rsBalance = db.execQuery(queryBalance);
                    
                    if (rsBalance.next()) {
                        balance = rsBalance.getDouble("balance");
                    }
                    
                    user = new Customer(id, name, email, password, phone, addr, balance);
                    
                } else if (role.equals("Admin")) {
                    // buat object admin (kontak darurat default dulu)
                    user = new model.Admin(id, name, email, password, phone, addr, "-");
                    
                } else if (role.equals("Courier")) {
                    // buat object courier (info kendaraan default dulu)
                    user = new model.Courier(id, name, email, password, phone, addr, "Unknown", "Unknown");
                }
                
                // simpan user ke session biar bisa diakses global
                util.Session.getInstance().setUser(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return user;
    }
    
    // method ini untuk ambil data user spesifik berdasarkan id
    public User getUser(String idUser) {
        String query = "SELECT * FROM Users WHERE idUser = '" + idUser + "'";
        ResultSet rs = db.execQuery(query);
        try { if (rs.next()) return null; } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    
    // method ini untuk update profil user (cuma nama, hp, alamat yg boleh diedit)
    public String editProfile(String idUser, String newName, String newPhone, String newAddress) {
        
        // validasi input lagi, pastiin ga ada yg kosong
        if (newName.isEmpty()) return "Name cannot be empty";
        if (newAddress.isEmpty()) return "Address cannot be empty";
        
        if (newPhone.isEmpty()) return "Phone cannot be empty";
        if (newPhone.length() < 10 || newPhone.length() > 13) return "Phone must be 10-13 digits";
        if (!isNumeric(newPhone)) return "Phone must be numeric";

        // update data di database
        String query = String.format("UPDATE Users SET fullName = '%s', phone = '%s', address = '%s' WHERE idUser = '%s'",
                newName, newPhone, newAddress, idUser);
        
        try {
            db.execUpdate(query);

            // update data di session biar ui langsung berubah
            User currentUser = Session.getInstance().getUser();
            if (currentUser != null) {
                currentUser.setFullName(newName);
                currentUser.setPhone(newPhone);
                currentUser.setAddress(newAddress);
            }
            return "Success";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "Database Error";
        }
    }

    // helper method untuk generate id customer otomatis (misal CU001)
    private String generateId() {
        String query = "SELECT idUser FROM Users WHERE role = 'Customer' ORDER BY idUser DESC LIMIT 1";
        ResultSet rs = db.execQuery(query);
        try {
            if (rs.next()) {
                String lastId = rs.getString("idUser");
                int number = Integer.parseInt(lastId.substring(2)) + 1;
                return String.format("CU%03d", number);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "CU001"; 
    }

    // helper method untuk cek email udah terdaftar apa belum
    private boolean isEmailUnique(String email) {
        String query = "SELECT * FROM Users WHERE email = '" + email + "'";
        ResultSet rs = db.execQuery(query);
        try {
            return !rs.next(); 
        } catch (SQLException e) {
            return false;
        }
    }

    // helper method untuk cek string isinya angka semua
    private boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }
}