package controller;

import model.Customer;
import util.Connect;
import util.Session;

public class CustomerHandler {
	
    // ambil koneksi db
    private Connect db = Connect.getInstance();

    // method ini sesuai sequence diagram message 1.1: topupbalance(amount)
    // parameternya string dulu karena dari view inputannya textfield/dialog (perlu validasi)
    public String topUpBalance(String amountStr) {
        
        // validasi input kosong (activity diagram: validate data)
        if (amountStr.isEmpty()) return "Amount must be filled";

        // validasi angka
        if (!isNumeric(amountStr)) return "Amount must be numeric";

        double amount = Double.parseDouble(amountStr);

        // validasi minimal top up (logic bisnis)
        if (amount < 10000) return "Minimum top up is Rp 10.000";

        // --- proses update sesuai sequence diagram ---
        
        // 1. ambil current user dari session
        Customer currentUser = (Customer) Session.getInstance().getUser();
        
        // 2. panggil method di model (message 1.1.1 di sequence diagram)
        // ini yang bikin kodingan kita "strict" sama diagram, ga cuma main setbalance doang
        currentUser.topUpBalance(amount);

        // 3. simpan perubahan ke database (message 1.1.1.1: saveda)
        // update saldo baru ke tabel customers
        String query = String.format("UPDATE Customers SET balance = %f WHERE idCustomer = '%s'", 
                currentUser.getBalance(), currentUser.getIdUser());
        
        try {
            db.execUpdate(query);
            // return sukses (message 1.2 di sequence diagram: mengembalikan info amount/sukses)
            return "Success";
        } catch (Exception e) {
            e.printStackTrace();
            // return error (message 1.3: errormessage di sequence diagram alt flow)
            return "Database Error";
        }
    }
    
    // method helper validasi angka
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }
}