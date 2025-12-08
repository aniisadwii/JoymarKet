package model;

public class Delivery {
    private String idOrder;
    private String idCourier;
    private String status;

    public Delivery(String idOrder, String idCourier, String status) {
        this.idOrder = idOrder;
        this.idCourier = idCourier;
        this.status = status;
    }

    // --- METHOD STUB SESUAI CLASS DIAGRAM ---
    
    public void createDelivery(String idOrder, String idCourier) {
        // Logic di Handler
    }

    public Delivery getDelivery(String idOrder, String idCourier) {
        return null;
    }

    // UPDATE: Tambahkan parameter idCourier sesuai Class Diagram
    public void editDeliveryStatus(String idOrder, String idCourier, String status) {
        // Logic di Handler
    }

    // --- GETTERS & SETTERS ---
    public String getIdOrder() { return idOrder; }
    public String getIdCourier() { return idCourier; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}