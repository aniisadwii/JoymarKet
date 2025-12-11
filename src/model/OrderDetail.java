package model;

import java.util.List;
import java.util.ArrayList;

public class OrderDetail {
    private String idOrder;
    private String idProduct;
    private int qty;

    // konstruktor ini berfungsi untuk menginisialisasi objek detail pesanan yang berisi relasi antara id pesanan, produk, dan jumlahnya
    public OrderDetail(String idOrder, String idProduct, int qty) {
        this.idOrder = idOrder;
        this.idProduct = idProduct;
        this.qty = qty;
    }
    
    // --- metode stub (kerangka) sesuai diagram kelas ---

    // metode ini disediakan agar struktur kelas sesuai dengan rancangan diagram, namun logika penyimpanan data detail pesanan ke database sepenuhnya ditangani oleh orderhandler
    public void createOrderDetail(String idOrder, String idProduct, int qty) {
    	
    }

    // metode ini merepresentasikan fungsi pengambilan data detail pesanan, implementasi query select dijalankan di sisi controller
    public OrderDetail getOrderDetail(String idOrder, String idProduct) {
        return null; 
    }

    // metode ini berfungsi sebagai representasi pengambilan detail pesanan khusus pelanggan, logika pencariannya didelegasikan ke handler
    public OrderDetail getCustomerOrderDetail(String idOrder, String idProduct) {
        return null;
    }

    // --- getter dan setter ---
	public String getIdOrder() {
		return idOrder;
	}

	public void setIdOrder(String idOrder) {
		this.idOrder = idOrder;
	}

	public String getIdProduct() {
		return idProduct;
	}

	public void setIdProduct(String idProduct) {
		this.idProduct = idProduct;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}
}