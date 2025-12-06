package view;

import controller.CartItemHandler;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.CartItem;
import model.Product;
import util.Session;

public class CartItemWindow {

    private CartItemHandler cartHandler = new CartItemHandler();
    private VBox layout;
    
    private Product product;   
    private CartItem cartItem; 
    private boolean isEditMode = false;
    
    private Runnable onUpdateSuccess; 

    public CartItemWindow(Product product) {
        this.product = product;
        this.isEditMode = false;
        initialize();
    }

    public CartItemWindow(CartItem cartItem, Runnable onUpdateSuccess) {
        this.cartItem = cartItem;
        this.isEditMode = true;
        this.onUpdateSuccess = onUpdateSuccess; 
        initialize();
    }

    private void initialize() {
        layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.CENTER_LEFT);
        layout.setStyle("-fx-border-color: #ccc; -fx-border-width: 1px; -fx-background-color: #f9f9f9;");

        String itemName = isEditMode ? cartItem.getProductName() : product.getName();
        String currentQty = isEditMode ? String.valueOf(cartItem.getQuantity()) : "1";
        
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
            if (!isNumeric(qtyStr)) {
                showAlert(Alert.AlertType.ERROR, "Error", "Input must be a valid number");
                return;
            }

            int qty = Integer.parseInt(qtyStr);
            String userId = Session.getInstance().getUser().getIdUser();
            String status;
            
            if (isEditMode) {
                status = cartHandler.editCartItem(userId, cartItem.getIdProduct(), qty);
            } else {
                status = cartHandler.createCartItem(userId, product.getIdProduct(), qty);
            }

            if (status.equals("Success")) {
                showAlert(Alert.AlertType.INFORMATION, "Success", isEditMode ? "Quantity updated successfully" : "Successfully added to cart");
                
                if (!isEditMode) {
                    txtQty.setText(""); 
                } else {
                    if (onUpdateSuccess != null) {
                        onUpdateSuccess.run(); 
                    }
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed", status);
            }
        });

        layout.getChildren().addAll(lblName, inputBox);
    }

    public VBox getView() { return layout; }

    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        for (char c : str.toCharArray()) { if (!Character.isDigit(c)) return false; }
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}