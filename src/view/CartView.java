package view;

import controller.TransactionController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.CartItem;
import model.Customer;
import util.Session;

import java.util.List;

public class CartView {
    private Stage stage;
    private TransactionController transactionController = new TransactionController();
    private Customer currentUser;

    public CartView(Stage stage) {
        this.stage = stage;
        this.currentUser = (Customer) Session.getInstance().getUser();
        initialize();
    }

    private void initialize() {
        Label lblTitle = new Label("Your Cart 🛒");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Tabel Keranjang
        TableView<CartItem> table = new TableView<>();
        
        TableColumn<CartItem, String> colName = new TableColumn<>("Product");
        colName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colName.setMinWidth(150);

        TableColumn<CartItem, Integer> colQty = new TableColumn<>("Qty");
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<CartItem, Double> colPrice = new TableColumn<>("Price");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<CartItem, Double> colTotal = new TableColumn<>("Subtotal");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        table.getColumns().addAll(colName, colQty, colPrice, colTotal);

        // Load Data
        refreshTable(table);

        // Bagian Bawah (Total & Checkout)
        Label lblTotal = new Label("Total: calculating...");
        calculateTotalDisplay(table, lblTotal);

        Button btnCheckout = new Button("Checkout Now");
        Button btnBack = new Button("Back to Home");
        
        // Logic Checkout
        btnCheckout.setOnAction(e -> {
            if (table.getItems().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Empty", "Belanja dulu dong bestie!");
                return;
            }
            
            // Konfirmasi dulu
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Yakin mau bayar? Saldo bakal kepotong nih.", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait();

            if (confirm.getResult() == ButtonType.YES) {
                String result = transactionController.checkout(currentUser.getIdUser());
                
                if (result.equals("Success")) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Checkout Berhasil! Pesanan sedang diproses kurir 🛵");
                    new CustomerMainView(stage); // Balik ke Home
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed", result);
                }
            }
        });

        btnBack.setOnAction(e -> new CustomerMainView(stage));

        HBox buttonBox = new HBox(10, btnBack, btnCheckout);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(lblTitle, table, lblTotal, buttonBox);

        Scene scene = new Scene(layout, 600, 500);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Cart");
    }

    private void refreshTable(TableView<CartItem> table) {
        List<CartItem> items = transactionController.getCartItems(currentUser.getIdUser());
        table.getItems().setAll(items);
    }
    
    private void calculateTotalDisplay(TableView<CartItem> table, Label label) {
        double total = 0;
        for (CartItem item : table.getItems()) {
            total += item.getTotal();
        }
        label.setText("Total Payment: Rp " + total);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}