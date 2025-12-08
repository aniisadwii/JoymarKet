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
	
	// mengambil instance koneksi database
    private Connect db = Connect.getInstance();

    // method ini disesuaikan dengan sequence diagram yang hanya menerima 5 parameter
    // confirm password tidak disertakan karena sudah divalidasi di view
    // mengembalikan objek user jika sukses, sesuai panah return data pada diagram
    public User saveDataUser(String fullName, String email, String password, String phone, String address) throws Exception {
        
        // validasi nama tidak boleh kosong
        if (fullName.isEmpty()) throw new Exception("Full name cannot be empty");
        
        // validasi email harus diisi, format gmail, dan belum terdaftar
        if (email.isEmpty()) throw new Exception("Email must be filled");
        if (!email.endsWith("@gmail.com")) throw new Exception("Email must end with @gmail.com");
        if (!isEmailUnique(email)) throw new Exception("Email already registered");

        // validasi password minimal 6 karakter
        if (password.length() < 6) throw new Exception("Password must be at least 6 characters");
        
        // validasi nomor telepon harus angka dan panjangnya 10-13 digit
        if (phone.isEmpty()) throw new Exception("Phone must be filled");
        if (phone.length() < 10 || phone.length() > 13) throw new Exception("Phone must be 10-13 digits");
        if (!isNumeric(phone)) throw new Exception("Phone must be numeric");

        // validasi alamat tidak boleh kosong
        if (address.isEmpty()) throw new Exception("Address must be filled");

        // generate id customer baru secara otomatis
        String newId = generateId(); 
        
        // menyiapkan query untuk tabel users
        String queryUser = String.format("INSERT INTO Users VALUES ('%s', '%s', '%s', '%s', '%s', '%s', 'Customer')", 
                newId, fullName, email, password, phone, address);
        
        // menyiapkan query untuk tabel customers dengan saldo awal 0
        String queryCustomer = String.format("INSERT INTO Customers VALUES ('%s', 0)", newId);

        try {
            // eksekusi penyimpanan data ke database
            db.execUpdate(queryUser);
            db.execUpdate(queryCustomer);
            
            // mengembalikan objek customer baru sesuai dengan return value di sequence diagram
            return new Customer(newId, fullName, email, password, phone, address, 0.0);
            
        } catch (Exception e) {
            e.printStackTrace();
            // melempar exception jika terjadi kegagalan database sesuai alur error diagram
            throw new Exception("Database Error: Gagal menyimpan data."); 
        }
    }

 // --- method login (sesuai class diagram) ---
    // mengembalikan object user jika berhasil, null jika gagal.
    public User login(String email, String password) {
        
        // 1. validasi input kosong (sesuai tabel validasi di dokumen soal)
        if (email.isEmpty() || password.isEmpty()) {
            return null; // atau bisa throw exception jika ingin pesan spesifik
        }

        User user = null;
        
        // query cek user berdasarkan email dan password
        String query = String.format("SELECT * FROM Users WHERE email = '%s' AND password = '%s'", email, password);
        ResultSet rs = db.execQuery(query);

        try {
            if (rs.next()) {
                // ambil data dasar user
                String id = rs.getString("idUser");
                String name = rs.getString("fullName");
                String phone = rs.getString("phone");
                String addr = rs.getString("address");
                String role = rs.getString("role");
                
                // 2. inisialisasi object berdasarkan role
                if (role.equals("Customer")) {
                    // khusus customer, kita harus ambil saldo (balance) dari tabel customers
                    double balance = 0.0;
                    String queryBalance = "SELECT balance FROM Customers WHERE idCustomer = '" + id + "'";
                    ResultSet rsBalance = db.execQuery(queryBalance);
                    
                    if (rsBalance.next()) {
                        balance = rsBalance.getDouble("balance");
                    }
                    
                    user = new Customer(id, name, email, password, phone, addr, balance);
                    
                } else if (role.equals("Admin")) {
                    // untuk admin, sesuaikan dengan konstruktor model admin
                    // asumsi: emergency contact default strip "-" jika tidak diambil dari db
                    user = new model.Admin(id, name, email, password, phone, addr, "-");
                    
                } else if (role.equals("Courier")) {
                    // untuk courier, sesuaikan dengan konstruktor model courier
                    // asumsi: data kendaraan diambil nanti atau di-set default dulu
                    user = new model.Courier(id, name, email, password, phone, addr, "Unknown", "Unknown");
                }
                
                // 3. set session (assumption: controller yang set session biar praktis)
                util.Session.getInstance().setUser(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return user;
    }
    
    public User getUser(String idUser) {
        String query = "SELECT * FROM Users WHERE idUser = '" + idUser + "'";
        ResultSet rs = db.execQuery(query);
        try { if (rs.next()) return null; } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    
public String editProfile(String idUser, String newName, String newPhone, String newAddress) {
        
        // 1. Validasi Input (Cuma 3 field ini)
        if (newName.isEmpty()) return "Name cannot be empty";
        if (newAddress.isEmpty()) return "Address cannot be empty";
        
        if (newPhone.isEmpty()) return "Phone cannot be empty";
        if (newPhone.length() < 10 || newPhone.length() > 13) return "Phone must be 10-13 digits";
        if (!isNumeric(newPhone)) return "Phone must be numeric";

        // 2. Update Database (Query lebih pendek, gak usah update email/pass)
        String query = String.format("UPDATE Users SET fullName = '%s', phone = '%s', address = '%s' WHERE idUser = '%s'",
                newName, newPhone, newAddress, idUser);
        
        try {
            db.execUpdate(query);

            // 3. Update Session Data (Penting biar UI langsung berubah)
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

    // method helper untuk membuat id baru
    private String generateId() {
        // mengambil id terakhir dari database
        String query = "SELECT idUser FROM Users WHERE role = 'Customer' ORDER BY idUser DESC LIMIT 1";
        ResultSet rs = db.execQuery(query);
        try {
            if (rs.next()) {
                String lastId = rs.getString("idUser");
                // mengambil angka dari id terakhir dan ditambahkan 1
                int number = Integer.parseInt(lastId.substring(2)) + 1;
                // format id menjadi cu diikuti 3 digit angka
                return String.format("CU%03d", number);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // default id jika belum ada data
        return "CU001"; 
    }

    // method helper untuk mengecek keunikan email
    private boolean isEmailUnique(String email) {
        String query = "SELECT * FROM Users WHERE email = '" + email + "'";
        ResultSet rs = db.execQuery(query);
        try {
            // mengembalikan true jika data tidak ditemukan
            return !rs.next(); 
        } catch (SQLException e) {
            return false;
        }
    }

    // method helper untuk cek numerik tanpa regex sesuai ketentuan soal
    private boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }
}