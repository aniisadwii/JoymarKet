package model;

public class Product {
    private String idProduct;
    private String name;
    private double price;
    private int stock;
    private String category;

    // constructor buat object produk
    public Product(String idProduct, String name, double price, int stock, String category) {
        this.idProduct = idProduct;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }

    // getter
    public String getIdProduct() { 
    	return idProduct; 
    }
    
    public String getName() { 
    	return name; 
    }
    
    public double getPrice() { 
    	return price; 
    }
    
    public int getStock() { 
    	return stock; 
    }
    
    public String getCategory() { 
    	return category; 
    }
    
    // setter stok buat update
    public void setStock(int stock) { 
    	this.stock = stock; 
    }
    
    // override toString biar rapi pas dipake di combobox
    @Override
    public String toString() {
        return name + " - Rp" + price + " (Stock: " + stock + ")";
    }
}