package view;

import controller.ProductHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.Product;
import java.util.List;

public class ProductWindow { 
    
    private ProductHandler productHandler = new ProductHandler();
    private VBox layout;
    
    // Area dinamis buat nampilin CartItemWindow
    private VBox cartFormArea; 

    public ProductWindow() {
        initialize();
    }

    private void initialize() {
        layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label lblTitle = new Label("Available Products:");
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        ListView<Product> productListView = new ListView<>();
        List<Product> products = productHandler.getAllProducts(); 
        productListView.getItems().addAll(products);

        // Area kosong di bawah list
        cartFormArea = new VBox();
        cartFormArea.setPadding(new Insets(10, 0, 0, 0));
        
        // Listener: Kalau User KLIK produk di list
        productListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // 1. Bersihin area bawah
                cartFormArea.getChildren().clear();
                
                // 2. Instantiate CartItemWindow (Sesuai Sequence Diagram)
                CartItemWindow cartWindow = new CartItemWindow(newVal);
                
                // 3. Tampilkan View-nya
                cartFormArea.getChildren().add(cartWindow.getView());
            }
        });

        layout.getChildren().addAll(lblTitle, productListView, cartFormArea);
    }

    public VBox getView() {
        return layout;
    }
}