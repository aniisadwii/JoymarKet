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
    // method-method di bawah ini ada untuk memenuhi struktur Class Diagram.
    // implementasi logika sesungguhnya dilakukan di class Controller (DeliveryHandler) 
    // agar sesuai dengan pola desain MVC (Pemisahan Model dan Logic).
    
    public void createDelivery(String idOrder, String idCourier) {
        // Logika INSERT ke database dijalankan oleh DeliveryHandler.assignCourierToOrder()
    }

    public Delivery getDelivery(String idOrder, String idCourier) {
        // Logika SELECT dari database dijalankan oleh DeliveryHandler.getDelivery()
        return null;
    }

    public void editDeliveryStatus(String idOrder, String idCourier, String status) {
        // Logika UPDATE ke database dijalankan oleh DeliveryHandler.editDeliveryStatus()
    }

    // --- getter dan setter ---
    public String getIdOrder() { return idOrder; }
    public String getIdCourier() { return idCourier; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}