package view;

import controller.ProductHandler;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
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

        Label lblTitle = new Label("Manage Product Stock 📦");
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Tabel Produk
        table = new TableView<>();
        
        TableColumn<Product, String> colName = new TableColumn<>("Product Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        TableColumn<Product, Integer> colStock = new TableColumn<>("Current Stock");
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        
        TableColumn<Product, Double> colPrice = new TableColumn<>("Price");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        table.getColumns().addAll(colName, colStock, colPrice);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Load Data Awal
        refreshTable();

        // Tombol Update (Refresh Manual DIHAPUS)
        Button btnUpdate = new Button("Update Stock ✏️");
        
        btnUpdate.setOnAction(e -> {
            Product selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "Pilih produk yang mau diupdate stoknya!");
                return;
            }
            
            // Logic Dialog
            TextInputDialog dialog = new TextInputDialog(String.valueOf(selected.getStock()));
            dialog.setTitle("Update Stock");
            dialog.setHeaderText("Update stok untuk: " + selected.getName());
            dialog.setContentText("Stok Baru:");
            
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(stockStr -> {
                // Validasi manual (No Regex)
                if (!isNumeric(stockStr)) {
                    showAlert(Alert.AlertType.ERROR, "Stok harus angka valid!");
                    return;
                }
                
                int newStock = Integer.parseInt(stockStr);
                
                // Panggil Method Handler
                String status = productHandler.editProductStock(selected.getIdProduct(), newStock);
                
                if (status.equals("Success")) {
                    showAlert(Alert.AlertType.INFORMATION, "Stok berhasil diupdate!");
                    refreshTable(); // <--- INI SUDAH CUKUP BUAT UPDATE TABEL
                } else {
                    showAlert(Alert.AlertType.ERROR, status);
                }
            });
        });
        
        // HBox Actions (Cuma isi btnUpdate aja sekarang)
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