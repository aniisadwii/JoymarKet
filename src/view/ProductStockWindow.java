package view;

import controller.ProductHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Product;
import java.util.Optional;

public class ProductStockWindow {
    
    private ProductHandler productHandler = new ProductHandler();
    private VBox layout;
    private TableView<Product> table;

    public ProductStockWindow() {
        initialize();
    }

    private void initialize() {
        layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label lblTitle = new Label("Manage Product Stock");
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        table = new TableView<>();
        
        TableColumn<Product, String> colName = new TableColumn<>("Product Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        TableColumn<Product, Integer> colStock = new TableColumn<>("Current Stock");
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        
        TableColumn<Product, Double> colPrice = new TableColumn<>("Price");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        table.getColumns().addAll(colName, colStock, colPrice);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        refreshTable();

        Button btnUpdate = new Button("Update Stock");
        
        btnUpdate.setOnAction(e -> {
            Product selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "Please select a product to update");
                return;
            }
            
            TextInputDialog dialog = new TextInputDialog(String.valueOf(selected.getStock()));
            dialog.setTitle("Update Stock");
            dialog.setHeaderText("Update stock for: " + selected.getName());
            dialog.setContentText("New Stock:");
            
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(stockStr -> {
                if (!isNumeric(stockStr)) {
                    showAlert(Alert.AlertType.ERROR, "Stock must be a valid number");
                    return;
                }
                
                int newStock = Integer.parseInt(stockStr);
                String status = productHandler.editProductStock(selected.getIdProduct(), newStock);
                
                if (status.equals("Success")) {
                    showAlert(Alert.AlertType.INFORMATION, "Stock updated successfully");
                    refreshTable(); 
                } else {
                    showAlert(Alert.AlertType.ERROR, status);
                }
            });
        });
        
        HBox actions = new HBox(10, btnUpdate);
        actions.setAlignment(Pos.CENTER_LEFT);
        
        layout.getChildren().addAll(lblTitle, table, actions);
    }

    public VBox getView() {
        return layout;
    }
    
    private void refreshTable() {
        table.getItems().setAll(productHandler.getAllProducts());
    }

    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String content) {
        Alert alert = new Alert(type);
        alert.setContentText(content);
        alert.show();
    }
}