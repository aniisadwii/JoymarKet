package model;

public abstract class User {
    protected String idUser;
    protected String fullName;
    protected String email;
    protected String password;
    protected String phone;
    protected String address;
    protected String role;

    public User(String idUser, String fullName, String email, String password, String phone, String address, String role) {
        this.idUser = idUser;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.role = role;
    }

    // --- REVISI: SESUAI DOCS (Hanya Nama, HP, Alamat) ---
    public void editProfile(String fullName, String phone, String address) {
        // Logic di UserHandler
    }

    public User getUser(String idUser) {
        return null;
    }

    // --- Getters & Setters ---
    public String getIdUser() { return idUser; }
    public void setIdUser(String idUser) { this.idUser = idUser; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}