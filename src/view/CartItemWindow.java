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
import model.Product;
import util.Session;
import java.util.List;

// SATU CLASS UNTUK SEMUA: Add, Edit, Remove, View List
// Sesuai Diagram Sequence yang selalu pakai nama 'CartItemWindow'
public class CartItemWindow {
    
    private Stage stage;
    private CartItemHandler cartHandler = new CartItemHandler();
    private OrderHandler orderHandler = new OrderHandler();
    private Customer currentUser;
    
    // Variabel buat Layout
    private VBox rootLayout;
    private VBox editArea; // Buat nampilin form di bawah tabel
    
    // Variabel khusus Mode Form (Add/Edit)
    private Product productTarget;
    private CartItem cartItemTarget;
    private boolean isEditMode = false;
    private Runnable onSuccessCallback;

    // --- CONSTRUCTOR 1: MODE HALAMAN KERANJANG (List + Remove) ---
    // Dipanggil dari CustomerWindow (Tombol "View Cart")
    public CartItemWindow(Stage stage) {
        this.stage = stage;
        this.currentUser = (Customer) Session.getInstance().getUser();
        initializeCartList(); // Bikin tampilan List Tabel
    }

    // --- CONSTRUCTOR 2: MODE FORM ADD/EDIT ---
    // Dipanggil dari ProductWindow (Add) atau dari Diri Sendiri (Edit)
    public CartItemWindow(Product product) {
        this.productTarget = product;
        this.isEditMode = false;
        initializeForm(); // Bikin tampilan Form Kecil
    }

    public CartItemWindow(CartItem cartItem, Runnable onSuccess) {
        this.cartItemTarget = cartItem;
        this.isEditMode = true;
        this.onSuccessCallback = onSuccess;
        initializeForm(); // Bikin tampilan Form Kecil
    }

    // --- TAMPILAN A: HALAMAN LIST KERANJANG (Fitur Remove & Checkout) ---
    private void initializeCartList() {
        Label lblTitle = new Label("My Cart");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TableView<CartItem> table = new TableView<>();
        setupTableColumns(table);

        Label lblTotal = new Label("Total: calculating...");
        refreshTable(table, lblTotal);
        
        TextField txtPromo = new TextField();
        txtPromo.setPromptText("Promo Code (Optional)");

        Button btnEdit = new Button("Edit Qty");
        Button btnDelete = new Button("Remove Item");
        Button btnCheckout = new Button("Checkout");
        Button btnBack = new Button("Back to Home");

        // Logic Tombol Edit (Memanggil Diri Sendiri mode Form)
        btnEdit.setOnAction(e -> {
            CartItem selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert(Alert.AlertType.WARNING, "Please select an item first"); return; }
            
            editArea.getChildren().clear();
            // REKUKSIF: Bikin object CartItemWindow baru tapi cuma ambil view form-nya aja
            CartItemWindow formView = new CartItemWindow(selected, () -> {
                refreshTable(table, lblTotal); 
                editArea.getChildren().clear(); 
            });
            editArea.getChildren().add(formView.getView());
        });

        // Logic Tombol Remove (Sesuai Diagram Remove)
        btnDelete.setOnAction(e -> {
            CartItem selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert(Alert.AlertType.WARNING, "Please select an item first"); return; }
            
            String status = cartHandler.deleteCartItem(currentUser.getIdUser(), selected.getIdProduct());
            
            if (status.equals("Success")) {
                showAlert(Alert.AlertType.INFORMATION, "Item removed successfully");
                refreshTable(table, lblTotal);
                editArea.getChildren().clear();
            } else {
                showAlert(Alert.AlertType.ERROR, status);
            }
        });

        // Logic Checkout
        btnCheckout.setOnAction(e -> {
            if (table.getItems().isEmpty()) { showAlert(Alert.AlertType.WARNING, "Cart is empty"); return; }
            String promoCode = txtPromo.getText().trim();
            
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Proceed to checkout?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait();

            if (confirm.getResult() == ButtonType.YES) {
                String idOrder = "OR" + System.currentTimeMillis();
                String result = orderHandler.checkout(idOrder, promoCode);
                
                if (result.equals("Success")) {
                    showAlert(Alert.AlertType.INFORMATION, "Subscription Successful"); 
                    new CustomerWindow(stage); 
                } else {
                    showAlert(Alert.AlertType.ERROR, result);
                }
            }
        });

        btnBack.setOnAction(e -> new CustomerWindow(stage)); 

        HBox itemActions = new HBox(10, btnEdit, btnDelete);
        HBox mainActions = new HBox(10, btnBack, btnCheckout);
        mainActions.setAlignment(Pos.CENTER_RIGHT);
        
        editArea = new VBox();
        editArea.setPadding(new Insets(10));
        editArea.setStyle("-fx-background-color: #f0f0f0;"); 

        rootLayout = new VBox(10);
        rootLayout.setPadding(new Insets(20));
        rootLayout.getChildren().addAll(lblTitle, table, itemActions, editArea, lblTotal, new Label("Promo Code:"), txtPromo, mainActions);

        Scene scene = new Scene(rootLayout, 600, 600);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Cart");
        stage.show();
    }

    // --- TAMPILAN B: FORM KECIL (Fitur Add & Update) ---
    private void initializeForm() {
        rootLayout = new VBox(10);
        rootLayout.setPadding(new Insets(15));
        rootLayout.setAlignment(Pos.CENTER_LEFT);
        rootLayout.setStyle("-fx-border-color: #ccc; -fx-border-width: 1px; -fx-background-color: #f9f9f9;");

        String itemName = isEditMode ? cartItemTarget.getProductName() : productTarget.getName();
        String currentQty = isEditMode ? String.valueOf(cartItemTarget.getQuantity()) : "1";
        
        Label lblName = new Label(isEditMode ? "Edit Item: " + itemName : "Selected: " + itemName);
        lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER_LEFT);
        
        Label lblQty = new Label("Quantity:");
        TextField txtQty = new TextField(currentQty); 
        txtQty.setPrefWidth(60);

        Button btnAction = new Button(isEditMode ? "Update Qty" : "Add to Cart");

        inputBox.getChildren().addAll(lblQty, txtQty, btnAction);

        btnAction.setOnAction(e -> {
            String qtyStr = txtQty.getText();
            if (!isNumeric(qtyStr)) { showAlert(Alert.AlertType.ERROR, "Input must be a valid number"); return; }

            int qty = Integer.parseInt(qtyStr);
            String userId = Session.getInstance().getUser().getIdUser();
            String status;
            
            // Sesuai Diagram: Add (createCartItem) atau Update (editCartItem)
            if (isEditMode) {
                status = cartHandler.editCartItem(userId, cartItemTarget.getIdProduct(), qty);
            } else {
                status = cartHandler.createCartItem(userId, productTarget.getIdProduct(), qty);
            }

            if (status.equals("Success")) {
                showAlert(Alert.AlertType.INFORMATION, "Success", isEditMode ? "Quantity updated" : "Added to cart");
                if (isEditMode && onSuccessCallback != null) onSuccessCallback.run();
                if (!isEditMode) txtQty.setText("");
            } else {
                showAlert(Alert.AlertType.ERROR, status);
            }
        });

        rootLayout.getChildren().addAll(lblName, inputBox);
    }

    // --- HELPER METHODS ---
    public VBox getView() { return rootLayout; } // Buat diambil sama ProductWindow

    private void setupTableColumns(TableView<CartItem> table) {
        TableColumn<CartItem, String> colName = new TableColumn<>("Product");
        colName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        
        TableColumn<CartItem, Integer> colQty = new TableColumn<>("Qty");
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        
        TableColumn<CartItem, Double> colPrice = new TableColumn<>("Price");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        TableColumn<CartItem, Double> colTotal = new TableColumn<>("Subtotal");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        table.getColumns().addAll(colName, colQty, colPrice, colTotal);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void refreshTable(TableView<CartItem> table, Label label) {
        List<CartItem> items = cartHandler.getCartItems(currentUser.getIdUser());
        table.getItems().setAll(items);
        double total = 0;
        for (CartItem item : items) total += item.getTotal();
        label.setText("Total Payment: Rp " + total);
    }
    
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        for (char c : str.toCharArray()) { if (!Character.isDigit(c)) return false; }
        return true;
    }

    private void showAlert(Alert.AlertType type, String content) {
        Alert alert = new Alert(type);
        alert.setContentText(content);
        alert.show();
    }
    
    // Overload showAlert buat title custom
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}