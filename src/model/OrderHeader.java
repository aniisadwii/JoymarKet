package model;

import java.sql.Timestamp;

// kelas ini merepresentasikan header transaksi, diganti namanya jadi OrderHeader sesuai class diagram
public class OrderHeader {
    private String idOrder;
    private String idCustomer;
    private String idPromo; 
    private String status;
    private Timestamp orderedAt;
    private double totalAmount;

    // konstruktor lengkap dengan id promo untuk inisialisasi data header pesanan baru
    public OrderHeader(String idOrder, String idCustomer, String idPromo, double totalAmount, String status, Timestamp orderedAt) {
        this.idOrder = idOrder;
        this.idCustomer = idCustomer;
        this.idPromo = idPromo;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderedAt = orderedAt;
    }
    
    // konstruktor overloading untuk kompatibilitas dengan kode lama yang mungkin tidak menggunakan promo
    public OrderHeader(String idOrder, String idCustomer, double totalAmount, String status, Timestamp orderedAt) {
        this(idOrder, idCustomer, null, totalAmount, status, orderedAt);
    }

    // --- metode wajib sesuai diagram kelas ---
    
    // metode ini dibuat untuk memenuhi struktur diagram kelas, adapun proses insert data header ke database dijalankan oleh orderhandler
    public void createOrderHeader(String idCustomer, String idPromo) {
        this.idCustomer = idCustomer;
        this.idPromo = idPromo;
    }
    
    // metode ini merepresentasikan perubahan status pesanan pada model, eksekusi query update status dilakukan oleh deliveryhandler atau orderhandler
    public void editOrderHeaderStatus(String idOrder, String status) {
        this.status = status;
    }

    // --- getter untuk mengambil data atribut ---
    public String getIdOrder() { 
    	return idOrder; 
    }
    
    public String getIdCustomer() { 
    	return idCustomer; 
    }
    
    public String getIdPromo() { 
    	return idPromo; 
    }
    
    public double getTotalAmount() { 
    	return totalAmount; 
    }
    
    public String getStatus() { 
    	return status; 
    }
    
    public Timestamp getOrderedAt() { 
    	return orderedAt; 
    }
    
    // metode bantuan untuk menampilkan tanggal dalam format string agar mudah dibaca di tabel
    public String getDateStr() { 
    	return orderedAt.toString(); 
    }
}