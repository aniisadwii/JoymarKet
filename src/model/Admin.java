package model;

public class Admin extends User {
    private String emergencyContact;

    // constructor ini buat inisialisasi object admin
    public Admin(String idUser, String fullName, String email, String password, String phone, String address, String emergencyContact) {
        // panggil constructor parent dan set role jadi admin
        super(idUser, fullName, email, password, phone, address, "Admin");
        this.emergencyContact = emergencyContact;
    }

    // --- getter dan setter ---
    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }
}