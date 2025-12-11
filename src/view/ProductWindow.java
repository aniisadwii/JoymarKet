package view;

import controller.ProductHandler;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.Product;
import util.Session;
import java.util.Optional;

// class ini bersifat multifungsi, dipakai untuk fitur view products (customer) dan edit stock (admin)
public class ProductWindow { 
    
    private ProductHandler productHandler = new ProductHandler();
    private VBox layout;
    private TableView<Product> table;
    
    // area khusus di bawah tabel untuk menampilkan form add to cart bagi customer
    private VBox cartFormArea; 

    // constructor ini untuk inisialisasi window produk
    public ProductWindow() {
        initialize();
    }

    // method ini untuk menyusun layout, tabel produk, dan handling interaksi user sesuai role
    private void initialize() {
        layout = new VBox(10);
        layout.setPadding(new Insets(10));

        // judul window menyesuaikan role user yang sedang login
        String role = Session.getInstance().getUser().getRole();
        Label lblTitle = new Label(role.equals("Admin") ? "Manage Product Stock" : "Available Products");
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // inisialisasi tabel untuk menampilkan daftar produk
        table = new TableView<>();
        setupTableColumns();
        refreshTable();

        // area container untuk form cart
        cartFormArea = new VBox();
        cartFormArea.setPadding(new Insets(10, 0, 0, 0));

        // handling event klik pada baris tabel
        table.setOnMouseClicked(e -> {
            // ambil item produk yang diklik
            Product selected = table.getSelectionModel().getSelectedItem();
            
            // pastikan item valid (bukan klik area kosong)
            if (selected != null) {
                if (role.equals("Admin")) {
                    // jika admin, jalankan logika edit stok
                    handleAdminAction(selected);
                    
                    // opsional: hapus seleksi setelah aksi selesai agar rapi
                    table.getSelectionModel().clearSelection(); 
                } else {
                    // jika customer, tampilkan form add to cart di bagian bawah
                    handleCustomerAction(selected);
                }
            }
        });

        layout.getChildren().addAll(lblTitle, table, cartFormArea);
    }

    // method ini untuk konfigurasi kolom-kolom pada tabel produk
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

    // method ini untuk menangani aksi admin saat ingin mengubah stok produk via dialog input
    private void handleAdminAction(Product product) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(product.getStock()));
        dialog.setTitle("Update Stock");
        dialog.setHeaderText("Update stock for: " + product.getName());
        dialog.setContentText("New Stock:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(stockStr -> {
            if (!isNumeric(stockStr)) {
                showAlert(Alert.AlertType.ERROR, "Stock must be a valid number");
                return;
            }
            
            int newStock = Integer.parseInt(stockStr);
            // panggil controller untuk update stok di database
            String status = productHandler.editProductStock(product.getIdProduct(), newStock);
            
            if (status.equals("Success")) {
                showAlert(Alert.AlertType.INFORMATION, "Stock updated successfully");
                refreshTable(); 
            } else {
                showAlert(Alert.AlertType.ERROR, status);
            }
        });
    }

    // method ini untuk menangani aksi customer, memunculkan form cart di panel bawah
    private void handleCustomerAction(Product product) {
        cartFormArea.getChildren().clear();
        // panggil view CartItemWindow dalam mode form (reusable component)
        CartItemWindow formView = new CartItemWindow(product);
        cartFormArea.getChildren().add(formView.getView());
    }

    // method ini untuk memuat ulang data produk terbaru dari database
    public void refreshTable() {
        table.getItems().setAll(productHandler.getAllProducts());
    }

    // method ini mengembalikan layout utama untuk ditampilkan di scene
    public VBox getView() {
        return layout;
    }
    
    // helper untuk validasi input angka
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