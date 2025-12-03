package model;

public class CartItem {
    private String idProduct;
    private String productName;
    private double price;
    private int quantity;
    private double total;

    public CartItem(String idProduct, String productName, double price, int quantity) {
        this.idProduct = idProduct;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.total = price * quantity;
    }

    public String getIdProduct() { return idProduct; }
    public String getProductName() { return productName; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public double getTotal() { return total; }
    
    
    
    public void setIdProduct(String idProduct) {
		this.idProduct = idProduct;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.total = this.price * quantity;
    }
}