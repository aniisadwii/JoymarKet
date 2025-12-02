package model;

public class Admin extends User {
    private String emergencyContact;

    // Constructor
    public Admin(String idUser, String fullName, String email, String password, String phone, String address, String emergencyContact) {
        // "super" ini ngirim data ke constructor User (Parent)
        // Role kita hardcode jadi "Admin" biar otomatis
        super(idUser, fullName, email, password, phone, address, "Admin");
        this.emergencyContact = emergencyContact;
    }

    // Getter & Setter khusus Admin
    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }
}