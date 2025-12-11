package view;

import controller.CartItemHandler;
import controller.OrderHandler;
import controller.PromoHandler;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.CartItem;
import model.Customer;
import model.Product;
import model.Promo;
import util.Session;
import java.util.List;

// class ini untuk mengatur tampilan keranjang belanja (cart) dan form tambah/edit item
public class CartItemWindow {
    
    private Stage stage;
    private CartItemHandler cartHandler = new CartItemHandler();
    private OrderHandler orderHandler = new OrderHandler();
    private PromoHandler promoHandler = new PromoHandler(); 
    
    private Customer currentUser;
    
    private VBox rootLayout;
    private VBox editArea;
    
    private Product productTarget;
    private CartItem cartItemTarget;
    private boolean isEditMode = false;
    private Runnable onSuccessCallback;
    
    private double currentGrossTotal = 0; 
    private Label lblTotal; 
    private Label lblDiscountInfo; 

    // --- CONSTRUCTOR ---
    
    // constructor ini untuk inisialisasi halaman list keranjang utama
    public CartItemWindow(Stage stage) {
        this.stage = stage;
        this.currentUser = (Customer) Session.getInstance().getUser();
        initializeCartList(); 
    }

    // constructor ini untuk inisialisasi form tambah item ke keranjang (dari menu produk)
    public CartItemWindow(Product product) {
        this.productTarget = product;
        this.isEditMode = false;
        initializeForm(); 
    }

    // constructor ini untuk inisialisasi form edit quantity item yg udah ada di keranjang
    public CartItemWindow(CartItem cartItem, Runnable onSuccess) {
        this.cartItemTarget = cartItem;
        this.isEditMode = true;
        this.onSuccessCallback = onSuccess;
        initializeForm(); 
    }

    // --- TAMPILAN A: HALAMAN LIST KERANJANG ---
    
    // method ini untuk menyusun tampilan list item di keranjang beserta fitur checkout dan promo
    private void initializeCartList() {
        Label lblTitle = new Label("My Cart");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TableView<CartItem> table = new TableView<>();
        setupTableColumns(table);

        lblTotal = new Label("Total Payment: calculating...");
        lblTotal.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        lblDiscountInfo = new Label();
        lblDiscountInfo.setMinHeight(40); 

        refreshTable(table); 
        
        // --- FITUR PROMO ---
        TextField txtPromo = new TextField();
        txtPromo.setPromptText("Promo Code");
        
        Button btnApplyPromo = new Button("Apply"); 
        
        // logika untuk cek validitas kode promo dan hitung diskon
        Runnable applyPromoLogic = () -> {
            String code = txtPromo.getText().trim();

            if (code.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Please enter a promo code");
                return;
            }
            
            Promo promo = promoHandler.getPromo(code);
            
            if (promo != null) {
                double discountAmount = currentGrossTotal * (promo.getDiscountPercentage() / 100.0);
                double finalPrice = currentGrossTotal - discountAmount;
                
                lblDiscountInfo.setText(String.format("Promo Applied! Disc %.0f%% (-Rp %.2f)\nTotal Payment After Discount: Rp %.2f", 
                        promo.getDiscountPercentage(), discountAmount, finalPrice));
                lblDiscountInfo.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 13px;");
                
            } else {
                lblDiscountInfo.setText("Invalid Promo Code!");
                lblDiscountInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            }
        };

        // trigger cek promo saat tombol diklik atau tekan enter
        btnApplyPromo.setOnAction(e -> applyPromoLogic.run());
        txtPromo.setOnAction(e -> applyPromoLogic.run());
        
        HBox promoBox = new HBox(10, new Label("Promo Code:"), txtPromo, btnApplyPromo);
        promoBox.setAlignment(Pos.CENTER_LEFT);

        // --- BUTTONS ---
        Button btnEdit = new Button("Edit Qty");
        Button btnDelete = new Button("Remove Item");
        Button btnCheckout = new Button("Checkout");
        Button btnBack = new Button("Back to Home");

        // buka form edit quantity untuk item yg dipilih
        btnEdit.setOnAction(e -> {
            CartItem selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert(Alert.AlertType.WARNING, "Please select an item first"); return; }
            editArea.getChildren().clear();
            CartItemWindow formView = new CartItemWindow(selected, () -> {
                refreshTable(table); 
                editArea.getChildren().clear(); 
                lblDiscountInfo.setText(""); 
            });
            editArea.getChildren().add(formView.getView());
        });

        // hapus item dari keranjang via controller
        btnDelete.setOnAction(e -> {
            CartItem selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert(Alert.AlertType.WARNING, "Please select an item first"); return; }
            String status = cartHandler.deleteCartItem(currentUser.getIdUser(), selected.getIdProduct());
            if (status.equals("Success")) {
                showAlert(Alert.AlertType.INFORMATION, "Item removed successfully");
                refreshTable(table);
                editArea.getChildren().clear();
                lblDiscountInfo.setText(""); 
            } else {
                showAlert(Alert.AlertType.ERROR, status);
            }
        });

        // proses checkout, validasi keranjang kosong dan konfirmasi user
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
        
        rootLayout.getChildren().addAll(lblTitle, table, itemActions, editArea, lblTotal, promoBox, lblDiscountInfo, mainActions);

        // langsung masukkan rootlayout ke scene tanpa scrollpane
        Scene scene = new Scene(rootLayout, 600, 650);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Cart");
        stage.show();
    }

    // method ini untuk refresh data tabel dan hitung ulang total pembayaran
    private void refreshTable(TableView<CartItem> table) {
        List<CartItem> items = cartHandler.getCartItems(currentUser.getIdUser());
        table.getItems().setAll(items);
        
        currentGrossTotal = 0;
        for (CartItem item : items) {
            currentGrossTotal += item.getTotal();
        }
        
        lblTotal.setText("Total Payment: Rp " + currentGrossTotal);
    }

    // --- Helper Methods & Form Logic ---
    
    // method ini untuk menyusun form popup input quantity
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

        // logika simpan data ke keranjang (tambah baru atau update)
        btnAction.setOnAction(e -> {
            String qtyStr = txtQty.getText();
            if (!isNumeric(qtyStr)) { showAlert(Alert.AlertType.ERROR, "Input must be a valid number"); return; }

            int qty = Integer.parseInt(qtyStr);
            String userId = Session.getInstance().getUser().getIdUser();
            String status;
            
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

    public VBox getView() { return rootLayout; } 

    // method ini untuk setting kolom tabel cart
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
    
    // helper buat validasi angka
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
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}