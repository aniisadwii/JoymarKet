package model;

public class Customer extends User {
    private double balance;

    public Customer(String idUser, String fullName, String email, String password, String phone, String address, double balance) {
        // "super" manggil constructor parent (User)
        super(idUser, fullName, email, password, phone, address, "Customer");
        this.balance = balance;
    }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}