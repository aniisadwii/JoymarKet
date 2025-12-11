package model;

public class Promo {
    private String idPromo;
    private String code;
    private double discountPercentage;

    // constructor buat object promo
    public Promo(String idPromo, String code, double discountPercentage) {
        this.idPromo = idPromo;
        this.code = code;
        this.discountPercentage = discountPercentage;
    }

    // getter
    public String getIdPromo() { 
    	return idPromo; 
    }
    
    public String getCode() { 
    	return code; 
    }
    
    public double getDiscountPercentage() { 
    	return discountPercentage; 
    }
}