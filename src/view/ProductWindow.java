package view;

import controller.ProductHandler;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.Product;
import util.Session;
import java.util.Optional;

// CLASS PALUGADA: Dipakai di fitur 'View Products' (Customer) DAN 'Edit Product Stock' (Admin)
// Sesuai dengan nama lifeline 'ProductWindow' di kedua Sequence Diagram.
public class ProductWindow { 
    
    private ProductHandler productHandler = new ProductHandler();
    private VBox layout;
    private TableView<Product> table;
    
    // Area khusus buat Customer nampilin form cart (di bawah tabel)
    private VBox cartFormArea; 

    public ProductWindow() {
        initialize();
    }

    private void initialize() {
        layout = new VBox(10);
        layout.setPadding(new Insets(10));

        // Judul dinamis biar keren dikit
        String role = Session.getInstance().getUser().getRole();
        Label lblTitle = new Label(role.equals("Admin") ? "Manage Product Stock" : "Available Products");
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Kita pake TableView biar rapi (Standar Admin & Customer)
     // Kita pake TableView biar rapi
        table = new TableView<>();
        setupTableColumns();
        refreshTable();

        // Area Form Cart
        cartFormArea = new VBox();
        cartFormArea.setPadding(new Insets(10, 0, 0, 0));

        // --- REVISI BAGIAN INI ---
        // HAPUS bagian table.getSelectionModel().selectedItemProperty().addListener(...) yang lama
        // GANTI dengan setOnMouseClicked:
        
        table.setOnMouseClicked(e -> {
            // Ambil item yang diklik
            Product selected = table.getSelectionModel().getSelectedItem();
            
            // Pastikan itemnya ada (bukan klik area kosong)
            if (selected != null) {
                if (role.equals("Admin")) {
                    // Flow Admin: Edit Stock
                    handleAdminAction(selected);
                    
                    // Opsional: Lepas seleksi setelah klik biar rapi
                    table.getSelectionModel().clearSelection(); 
                } else {
                    // Flow Customer: Add to Cart
                    handleCustomerAction(selected);
                }
            }
        });

        layout.getChildren().addAll(lblTitle, table, cartFormArea);
    }

    // Setup kolom tabel
    private void setupTableColumns() {
        TableColumn<Product, String> colName = new TableColumn<>("Product Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        TableColumn<Product, Integer> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        
        TableColumn<Product, Double> colPrice = new TableColumn<>("Price");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        table.getColumns().addAll(colName, colStock, colPrice);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // Logic Khusus Admin (Pop-up Edit Stock)
    private void handleAdminAction(Product product) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(product.getStock()));
        dialog.setTitle("Update Stock");
        dialog.setHeaderText("Update stock for: " + product.getName());
        dialog.setContentText("New Stock:");
        
        // Agar selection tabel gak nyangkut/bisa diklik ulang
        // table.getSelectionModel().clearSelection(); // Opsional, tergantung selera UX

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(stockStr -> {
            if (!isNumeric(stockStr)) {
                showAlert(Alert.AlertType.ERROR, "Stock must be a valid number");
                return;
            }
            
            int newStock = Integer.parseInt(stockStr);
            // Panggil Controller (Sequence Message: editProductStock)
            String status = productHandler.editProductStock(product.getIdProduct(), newStock);
            
            if (status.equals("Success")) {
                showAlert(Alert.AlertType.INFORMATION, "Stock updated successfully");
                refreshTable(); 
            } else {
                showAlert(Alert.AlertType.ERROR, status);
            }
        });
    }

    // Logic Khusus Customer (Munculin Form Add to Cart di bawah)
    private void handleCustomerAction(Product product) {
        cartFormArea.getChildren().clear();
        // Panggil CartItemWindow (Mode Form) - Sesuai Diagram Add Cart
        CartItemWindow formView = new CartItemWindow(product);
        cartFormArea.getChildren().add(formView.getView());
    }

    public void refreshTable() {
        table.getItems().setAll(productHandler.getAllProducts());
    }

    public VBox getView() {
        return layout;
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
}