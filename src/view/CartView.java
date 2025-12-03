package view;

import controller.TransactionController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.CartItem;
import model.Customer;
import util.Session;

import java.util.List;
import java.util.Optional;

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
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Label Total
        Label lblTotal = new Label("Total: calculating...");
        
        refreshTable(table, lblTotal);
        
        TextField txtPromo = new TextField();
        txtPromo.setPromptText("Promo Code (Optional)");
        txtPromo.setPrefWidth(150);

        // --- BUTTONS ---
        Button btnEdit = new Button("Edit Qty ✏️");
        Button btnDelete = new Button("Remove Item 🗑️");
        Button btnCheckout = new Button("Checkout Now 💸");
        Button btnBack = new Button("Back to Home");

        // Logic Edit Qty
        btnEdit.setOnAction(e -> {
            CartItem selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "Pilih barang dulu!");
                return;
            }

            TextInputDialog dialog = new TextInputDialog(String.valueOf(selected.getQuantity()));
            dialog.setTitle("Edit Quantity");
            dialog.setHeaderText("Ubah jumlah " + selected.getProductName());
            dialog.setContentText("Qty Baru:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(qtyStr -> {
                if (!qtyStr.matches("\\d+")) {
                    showAlert(Alert.AlertType.ERROR, "Harus angka woi!");
                    return;
                }
                int newQty = Integer.parseInt(qtyStr);
                String status = transactionController.updateCartQty(currentUser.getIdUser(), selected.getIdProduct(), newQty);
                
                if (status.equals("Success")) {
                    refreshTable(table, lblTotal);
                } else {
                    showAlert(Alert.AlertType.ERROR, status);
                }
            });
        });

        // Logic Delete
        btnDelete.setOnAction(e -> {
            CartItem selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "Pilih barang dulu yang mau dibuang!");
                return;
            }
            
            transactionController.deleteCartItem(currentUser.getIdUser(), selected.getIdProduct());
            refreshTable(table, lblTotal);
        });

        // Logic Checkout
        btnCheckout.setOnAction(e -> {
            if (table.getItems().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Keranjang kosong melompong!");
                return;
            }
            
            // Ambil text promo
            String code = txtPromo.getText().trim();
            
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Yakin mau bayar?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait();

            if (confirm.getResult() == ButtonType.YES) {
                // Panggil checkout pake parameter kode promo
                String result = transactionController.checkout(currentUser.getIdUser(), code);
                
                if (result.equals("Success")) {
                    showAlert(Alert.AlertType.INFORMATION, "Checkout Berhasil! 🛵");
                    new CustomerMainView(stage);
                } else {
                    showAlert(Alert.AlertType.ERROR, result);
                }
            }
        });

        btnBack.setOnAction(e -> new CustomerMainView(stage));

        // Layout Tombol
        HBox itemActions = new HBox(10, btnEdit, btnDelete);
        itemActions.setAlignment(Pos.CENTER_LEFT);
        
        HBox mainActions = new HBox(10, btnBack, btnCheckout);
        mainActions.setAlignment(Pos.CENTER_RIGHT);
        
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(lblTitle, table, itemActions, lblTotal, new Label("Punya kode promo?"), txtPromo, mainActions);

        Scene scene = new Scene(layout, 600, 500);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Cart");
    }

    private void refreshTable(TableView<CartItem> table, Label label) {
        List<CartItem> items = transactionController.getCartItems(currentUser.getIdUser());
        table.getItems().setAll(items);
        
        double total = 0;
        for (CartItem item : items) total += item.getTotal();
        label.setText("Total Payment: Rp " + total);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
    }

    private void showAlert(Alert.AlertType type, String content) {
        Alert alert = new Alert(type);
        alert.setContentText(content);
        alert.show();
    }
}