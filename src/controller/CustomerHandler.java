package controller;

import model.Customer;
import util.Connect;
import util.Session;

public class CustomerHandler {
	private Connect db = Connect.getInstance();
    public String topUpBalance(String amountStr) {
        // 1. Validasi: Harus Diisi
        if (amountStr.isEmpty()) return "Amount must be filled";

        // 2. Validasi: Harus Angka
        if (!isNumeric(amountStr)) return "Amount must be numeric";

        double amount = Double.parseDouble(amountStr);

        // 3. Validasi: Minimal 10.000
        if (amount < 10000) return "Minimum top up is Rp 10.000";

        // Kalau lolos, update Database
        Customer currentUser = (Customer) Session.getInstance().getUser();
        String userId = currentUser.getIdUser();
        double newBalance = currentUser.getBalance() + amount;

        // Query Update
        String query = String.format("UPDATE Customers SET balance = %f WHERE idCustomer = '%s'", newBalance, userId);
        db.execUpdate(query);

        // PENTING: Update juga data di Session biar Real-time berubah di layar
        currentUser.setBalance(newBalance);
        
        return "Success";
    }
    
 // Helper Validasi Angka
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }
}
