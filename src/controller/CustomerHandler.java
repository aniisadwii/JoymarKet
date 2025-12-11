package controller;

import model.Customer;
import util.Connect;
import util.Session;

public class CustomerHandler {
    
    private Connect db = Connect.getInstance();

    // method ini untuk proses top up saldo, termasuk validasi
    public String topUpBalance(String amountStr) {
        
        // validasi input
        if (amountStr.isEmpty()) return "Amount must be filled";
        if (!isNumeric(amountStr)) return "Amount must be numeric";

        double amount = Double.parseDouble(amountStr);

        // minimal top up 10 ribu
        if (amount < 10000) return "Minimum top up is Rp 10.000";

        // --- proses update ---
        
        // 1. ambil current user
        Customer currentUser = (Customer) Session.getInstance().getUser();
        
        // 2. update di model
        currentUser.topUpBalance(amount);

        // 3. update di database
        String query = String.format("UPDATE Customers SET balance = %f WHERE idCustomer = '%s'", 
                currentUser.getBalance(), currentUser.getIdUser());
        
        try {
            db.execUpdate(query);
            return "Success";
        } catch (Exception e) {
            e.printStackTrace();
            return "Database Error";
        }
    }
    
    // helper method cek angka
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }
}