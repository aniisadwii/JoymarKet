package view;

import controller.ProductController;
import controller.TransactionController; // BARU
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Customer; // BARU
import model.Product;
import util.Session;

import java.util.List;
import java.util.Optional; // BARU

public class CustomerMainView {
    private Stage stage;
    private ProductController productController = new ProductController();
    private TransactionController transactionController = new TransactionController(); // BARU

    public CustomerMainView(Stage stage) {
        this.stage = stage;
        initialize();
    }

    private void initialize() {
        Customer customer = (Customer) Session.getInstance().getUser(); 

        // 1. Header & Balance Area
        Label lblWelcome = new Label("Welcome, " + customer.getFullName());
        lblWelcome.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label lblBalance = new Label("Balance: Rp " + customer.getBalance());
        lblBalance.setStyle("-fx-font-size: 14px; -fx-text-fill: green;");
        
        Button btnTopUp = new Button("Top Up (+)");
        
        // Logic Top Up (Ini udah bener urutannya)
        btnTopUp.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Top Up Balance");
            dialog.setHeaderText("Masukkan jumlah saldo (Min. 10.000)");
            dialog.setContentText("Amount:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(amountStr -> {
                String status = transactionController.topUp(amountStr);
                if (status.equals("Success")) {
                    lblBalance.setText("Balance: Rp " + customer.getBalance());
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Top Up Berhasil! Menyala dompetku 🔥");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed", status);
                }
            });
        });

        HBox balanceBox = new HBox(10, lblBalance, btnTopUp);

        // ---------------------------------------------------------
        // PINDAHIN BAGIAN INI KE SINI (SEBELUM LOGIC ADD TO CART)
        // ---------------------------------------------------------
        
        // 2. Product List & Components (DEKLARASI DULUAN)
        ListView<Product> productListView = new ListView<>();
        List<Product> products = productController.getAllProducts();
        productListView.getItems().addAll(products);
        productListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        Label lblInfo = new Label("Pilih produk untuk melihat detail / beli");
        Button btnAddToCart = new Button("Add to Cart"); // <-- Nah, tombolnya lahir di sini
        Button btnLogout = new Button("Logout");

        // ---------------------------------------------------------
        // BARU DEH LOGIC ADD TO CART-NYA DI BAWAH SINI
        // ---------------------------------------------------------
        
        btnAddToCart.setOnAction(e -> {
            // Sekarang productListView udah dikenal
            Product selectedProduct = productListView.getSelectionModel().getSelectedItem();

            if (selectedProduct == null) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Pilih produk dulu bestie!");
                return;
            }

            TextInputDialog dialog = new TextInputDialog("1");
            dialog.setTitle("Add to Cart");
            dialog.setHeaderText("Mau beli berapa " + selectedProduct.getName() + "?");
            dialog.setContentText("Quantity (Max: " + selectedProduct.getStock() + "):");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(qtyStr -> {
                if (!qtyStr.matches("\\d+")) {
                     showAlert(Alert.AlertType.ERROR, "Error", "Input harus angka!");
                     return;
                }

                int qty = Integer.parseInt(qtyStr);
                String userId = Session.getInstance().getUser().getIdUser();
                
                String status = transactionController.addToCart(userId, selectedProduct.getIdProduct(), qty);

                if (status.equals("Success")) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Berhasil masuk keranjang! 🛒");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed", status);
                }
            });
        });

        // Event Logout
        btnLogout.setOnAction(e -> {
            Session.getInstance().setUser(null);
            new LoginView(stage);
        });

        // 4. Layout (Add to children)
        VBox centerLayout = new VBox(10);
        centerLayout.setPadding(new Insets(10));
        centerLayout.getChildren().addAll(new Label("Available Products:"), productListView, btnAddToCart);

        VBox topLayout = new VBox(10);
        topLayout.setPadding(new Insets(10));
        topLayout.setStyle("-fx-background-color: #f0f0f0;");
        topLayout.getChildren().addAll(lblWelcome, balanceBox, lblInfo, btnLogout); 

        BorderPane root = new BorderPane();
        root.setTop(topLayout);
        root.setCenter(centerLayout);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Customer Dashboard");
        stage.show();
    }
    
    // Helper buat nampilin Alert
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}