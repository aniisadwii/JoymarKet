package model;

import java.sql.Timestamp;

// 1. RENAME CLASS: Jadi OrderHeader (Sesuai Class Diagram)
public class OrderHeader {
    private String idOrder;
    private String idCustomer;
    private String idPromo; 
    private String status;
    private Timestamp orderedAt;
    private double totalAmount;

    // Constructor
    public OrderHeader(String idOrder, String idCustomer, String idPromo, double totalAmount, String status, Timestamp orderedAt) {
        this.idOrder = idOrder;
        this.idCustomer = idCustomer;
        this.idPromo = idPromo;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderedAt = orderedAt;
    }
    
    // Constructor Overloading (biar codingan lama yg gak pake promo tetep jalan, opsional)
    public OrderHeader(String idOrder, String idCustomer, double totalAmount, String status, Timestamp orderedAt) {
        this(idOrder, idCustomer, null, totalAmount, status, orderedAt);
    }

    // --- METHOD WAJIB SESUAI CLASS DIAGRAM ---
    // Method ini ada di diagram di dalam kotak OrderHeader.
    // Kita taruh sini buat menuhin syarat, isinya simple aja atau return object.
    public void createOrderHeader(String idCustomer, String idPromo) {
        this.idCustomer = idCustomer;
        this.idPromo = idPromo;
        // Logic penyimpanan aslinya tetep di Controller (OrderHandler) biar MVC murni.
    }
    
    // Method lain sesuai diagram (edit, get) bisa dikosongin logicnya atau setter/getter
    public void editOrderHeaderStatus(String idOrder, String status) {
        this.status = status;
    }

    // Getters
    public String getIdOrder() { return idOrder; }
    public String getIdCustomer() { return idCustomer; }
    public String getIdPromo() { return idPromo; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public Timestamp getOrderedAt() { return orderedAt; }
    
    // Helper buat TableView
    public String getDateStr() { return orderedAt.toString(); }
}