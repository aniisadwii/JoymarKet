package view;

import controller.CartItemHandler;
import controller.OrderHandler;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.CartItem;
import model.Customer;
import util.Session;
import java.util.List;

// Nama Class Sesuai Sequence Diagram (OrderHeaderWindow)
public class OrderHeaderWindow {
    private Stage stage;
    private CartItemHandler cartHandler = new CartItemHandler();
    private OrderHandler orderHandler = new OrderHandler();
    private Customer currentUser;
    
    private VBox editArea; 

    public OrderHeaderWindow(Stage stage) {
        this.stage = stage;
        this.currentUser = (Customer) Session.getInstance().getUser();
        initialize();
    }

    private void initialize() {
        Label lblTitle = new Label("Checkout / Cart 🛒");
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

        Label lblTotal = new Label("Total: calculating...");
        refreshTable(table, lblTotal);
        
        TextField txtPromo = new TextField();
        txtPromo.setPromptText("Promo Code (Optional)");

        Button btnEdit = new Button("Edit Qty ✏️");
        Button btnDelete = new Button("Remove Item 🗑️");
        Button btnCheckout = new Button("Checkout Now 💸");
        Button btnBack = new Button("Back to Home");
        Button btnRefresh = new Button("Refresh Data 🔄"); 

        // --- Logic Buttons ---
        
        btnEdit.setOnAction(e -> {
            CartItem selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert(Alert.AlertType.WARNING, "Pilih barang dulu!"); return; }
            editArea.getChildren().clear();
            CartItemWindow editWindow = new CartItemWindow(selected, () -> {
                refreshTable(table, lblTotal); 
                editArea.getChildren().clear(); 
            });
            editArea.getChildren().add(editWindow.getView());
        });

        btnRefresh.setOnAction(e -> refreshTable(table, lblTotal));

        btnDelete.setOnAction(e -> {
            CartItem selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert(Alert.AlertType.WARNING, "Pilih barang!"); return; }
            
            String status = cartHandler.deleteCartItem(currentUser.getIdUser(), selected.getIdProduct());
            if (status.equals("Success")) {
                showAlert(Alert.AlertType.INFORMATION, "Berhasil dihapus! 🗑️");
                refreshTable(table, lblTotal);
                editArea.getChildren().clear();
            } else {
                showAlert(Alert.AlertType.ERROR, status);
            }
        });

        // --- LOGIC CHECKOUT (YANG INI UPDATE) ---
        btnCheckout.setOnAction(e -> {
            if (table.getItems().isEmpty()) { showAlert(Alert.AlertType.WARNING, "Keranjang kosong!"); return; }
            
            String promoCode = txtPromo.getText().trim();
            
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Yakin mau bayar?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait();

            if (confirm.getResult() == ButtonType.YES) {
                // 1. GENERATE ID ORDER Manual (Tanpa UUID)
                // Format: OR + Timestamp (Contoh: OR1709882233) -> Dijamin unik & Angka
                String idOrder = "OR" + System.currentTimeMillis();

                // 2. Panggil Method
                String result = orderHandler.checkout(idOrder, promoCode);
                
                if (result.equals("Success")) {
                    showAlert(Alert.AlertType.INFORMATION, "Subscription Successful"); 
                    new CustomerWindow(stage); 
                } else {
                    if (result.contains("Balance")) {
                         showAlert(Alert.AlertType.ERROR, "Insufficient Balance");
                    } else {
                         showAlert(Alert.AlertType.ERROR, result);
                    }
                }
            }
        });

        btnBack.setOnAction(e -> new CustomerWindow(stage)); 

        HBox itemActions = new HBox(10, btnEdit, btnDelete, btnRefresh);
        itemActions.setAlignment(Pos.CENTER_LEFT);
        HBox mainActions = new HBox(10, btnBack, btnCheckout);
        mainActions.setAlignment(Pos.CENTER_RIGHT);
        
        editArea = new VBox();
        editArea.setPadding(new Insets(10));
        editArea.setStyle("-fx-background-color: #f0f0f0;"); 

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(lblTitle, table, itemActions, editArea, lblTotal, new Label("Punya kode promo?"), txtPromo, mainActions);

        Scene scene = new Scene(layout, 600, 600);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Checkout");
        stage.show();
    }

    private void refreshTable(TableView<CartItem> table, Label label) {
        List<CartItem> items = cartHandler.getCartItems(currentUser.getIdUser());
        table.getItems().setAll(items);
        double total = 0;
        for (CartItem item : items) total += item.getTotal();
        label.setText("Total Payment: Rp " + total);
    }
    
    private void showAlert(Alert.AlertType type, String content) {
        Alert alert = new Alert(type);
        alert.setContentText(content);
        alert.show();
    }
}