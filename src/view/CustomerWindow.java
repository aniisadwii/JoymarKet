package view;

import controller.CustomerHandler;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Customer;
import util.Session;
import java.util.Optional;

// class ini merepresentasikan dashboard utama untuk customer
public class CustomerWindow {
    
    private Stage stage;
    private CustomerHandler customerHandler = new CustomerHandler(); 

    // constructor ini untuk inisialisasi window dashboard customer
    public CustomerWindow(Stage stage) {
        this.stage = stage;
        initialize();
    }

    // method ini untuk menyusun layout ui, menampilkan info saldo, dan navigasi menu
    private void initialize() {
        // ambil data customer yang sedang login dari session
        Customer customer = (Customer) Session.getInstance().getUser(); 

        Label lblWelcome = new Label("Welcome, " + customer.getFullName());
        lblWelcome.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // label saldo ini nanti akan diupdate realtime setelah top up berhasil
        Label lblBalance = new Label("Balance: Rp " + customer.getBalance());
        lblBalance.setStyle("-fx-font-size: 14px; -fx-text-fill: green;");
        
        Button btnTopUp = new Button("Top Up");
        
        // logika tombol top up, mengimplementasikan alur activity diagram
        btnTopUp.setOnAction(e -> {
            // tampilkan dialog input nominal top up
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Top Up Balance");
            dialog.setHeaderText("Enter Top Up Amount (Min. 10.000)");
            dialog.setContentText("Amount:");

            // proses input user setelah tekan ok
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(amountStr -> {
                
                // panggil controller untuk validasi dan update saldo di database
                String status = customerHandler.topUpBalance(amountStr); 
                
                // cek hasil transaksi (decision node di activity diagram)
                if (status.equals("Success")) {
                    // update tampilan saldo langsung di ui dan kasih pesan sukses
                    lblBalance.setText("Balance: Rp " + customer.getBalance()); 
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Top Up Successful");
                } else {
                    // tampilkan pesan error jika validasi gagal
                    showAlert(Alert.AlertType.ERROR, "Failed", status);
                }
            });
        });

        // tombol navigasi ke menu lain (cart, profile, history, logout)
        Button btnViewCart = new Button("View Cart");
        btnViewCart.setOnAction(e -> new CartItemWindow(stage));
        
        Button btnEditProfile = new Button("Edit Profile");
        btnEditProfile.setOnAction(e -> new UserWindow());
        
        Button btnHistory = new Button("Order History");
        btnHistory.setOnAction(e -> new OrderHeaderWindow(stage));
        
        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> {
            Session.getInstance().setUser(null);
            new LoginView(stage);
        });

        HBox balanceBox = new HBox(10, lblBalance, btnTopUp);
        VBox topLayout = new VBox(10);
        topLayout.setPadding(new Insets(10));
        topLayout.setStyle("-fx-background-color: #f0f0f0;");
        topLayout.getChildren().addAll(lblWelcome, balanceBox, btnViewCart, btnHistory, btnEditProfile, btnLogout);

        // menampilkan list produk di area tengah menggunakan komponen reusable productwindow
        ProductWindow productWindow = new ProductWindow();
        
        BorderPane root = new BorderPane();
        root.setTop(topLayout);
        root.setCenter(productWindow.getView());

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Customer Dashboard");
        stage.show();
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}