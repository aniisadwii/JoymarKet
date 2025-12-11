package model;

public class Courier extends User {
    private String vehicleType;
    private String vehiclePlate;

    // constructor buat inisialisasi kurir dgn data kendaraan
    public Courier(String idUser, String fullName, String email, String password, String phone, String address, String vehicleType, String vehiclePlate) {
        super(idUser, fullName, email, password, phone, address, "Courier");
        this.vehicleType = vehicleType;
        this.vehiclePlate = vehiclePlate;
    }
    
    // --- getter dan setter ---
    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }

    public void setVehiclePlate(String vehiclePlate) {
        this.vehiclePlate = vehiclePlate;
    }
}