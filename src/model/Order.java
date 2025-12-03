package model;

import java.sql.Timestamp;

public class Order {
    private String idOrder;
    private String idCustomer;
    private double totalAmount;
    private String status;
    private Timestamp orderedAt;

    public Order(String idOrder, String idCustomer, double totalAmount, String status, Timestamp orderedAt) {
        this.idOrder = idOrder;
        this.idCustomer = idCustomer;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderedAt = orderedAt;
    }

    // Getters
    public String getIdOrder() { return idOrder; }
    public String getIdCustomer() { return idCustomer; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public Timestamp getOrderedAt() { return orderedAt; }
    
    // Property buat di TableView (kalo butuh String simpel)
    public String getDateStr() { return orderedAt.toString(); }
}