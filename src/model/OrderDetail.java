package model;

import java.util.List;
import java.util.ArrayList;

public class OrderDetail {
    private String idOrder;
    private String idProduct;
    private int qty;

    public OrderDetail(String idOrder, String idProduct, int qty) {
        this.idOrder = idOrder;
        this.idProduct = idProduct;
        this.qty = qty;
    }
    
    // 1. createOrderDetail(idOrder, idProduct, qty)

    // Isinya kita kosongin aja karena logic INSERT sebenarnya sudah dijalankan oleh OrderHandler.
    public void createOrderDetail(String idOrder, String idProduct, int qty) {
    	
    }

    // 2. getOrderDetail(idOrder, idProduct)
    public OrderDetail getOrderDetail(String idOrder, String idProduct) {
        return null; 
    }

    // 3. getCustomerOrderDetail(idOrder, idProduct)
    public OrderDetail getCustomerOrderDetail(String idOrder, String idProduct) {
        return null;
    }

    // Getters & Setters
    public String getIdOrder() { return idOrder; }
    public void setIdOrder(String idOrder) { this.idOrder = idOrder; }

    public String getIdProduct() { return idProduct; }
    public void setIdProduct(String idProduct) { this.idProduct = idProduct; }

    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }
}