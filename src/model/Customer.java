package model;

public class Customer extends User {
    private double balance;

    // constructor buat customer, ada data saldo
    public Customer(String idUser, String fullName, String email, String password, String phone, String address, double balance) {
        super(idUser, fullName, email, password, phone, address, "Customer");
        this.balance = balance;
    }

    // --- getter dan setter ---
    public double getBalance() { 
    	return balance; 
    }
    
    public void setBalance(double balance) { 
    	this.balance = balance; 
    }

    // method ini buat nambah saldo di object model sebelum save ke db
    public void topUpBalance(double amount) {
        this.balance += amount;
    }
}