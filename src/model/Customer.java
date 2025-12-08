package model;

public class Customer extends User {
    private double balance;

    public Customer(String idUser, String fullName, String email, String password, String phone, String address, double balance) {
        super(idUser, fullName, email, password, phone, address, "Customer");
        this.balance = balance;
    }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    // method ini wajib ada karena tertulis jelas di class diagram & sequence diagram (message 1.1.1)
    // fungsinya buat update state objek customer sebelum disimpan ke database
    public void topUpBalance(double amount) {
        this.balance += amount;
    }
}