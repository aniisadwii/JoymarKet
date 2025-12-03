package view;

import controller.AdminController;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Order;
import model.Product; // Jangan lupa import ini
import util.Session;

import java.util.List;
import java.util.Optional;

public class AdminMainView {
    private Stage stage;
    private AdminController adminController = new AdminController();

    public AdminMainView(Stage stage) {
        this.stage = stage;
        initialize();
    }

    private void initialize() {
        String name = Session.getInstance().getUser().getFullName();
        
        BorderPane root = new BorderPane();
        
        // Header
        Label lblHeader = new Label("Admin Dashboard - Halo, " + name);
        lblHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10;");
        
        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> {
            Session.getInstance().setUser(null);
            new LoginView(stage);
        });
        
        HBox topBar = new HBox(10, lblHeader, btnLogout);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: #ddd;");
        
        // --- TAB PANE ---
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE); // Biar tab gak bisa diclose
        
        // TAB 1: Manage Orders (Kodingan lama kita pindah sini)
        Tab tabOrders = new Tab("Incoming Orders 📦");
        tabOrders.setContent(createOrderContent());
        
        // TAB 2: Manage Stock (Fitur Baru)
        Tab tabStock = new Tab("Manage Stock 📝");
        tabStock.setContent(createStockContent());
        
        tabPane.getTabs().addAll(tabOrders, tabStock);
        
        root.setTop(topBar);
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Admin Panel");
        stage.show();
    }

    // --- LOGIC TAB 1 (ORDER) ---
    private VBox createOrderContent() {
        TableView<Order> table = new TableView<>();
        
        TableColumn<Order, String> colId = new TableColumn<>("Order ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idOrder"));

        TableColumn<Order, String> colCust = new TableColumn<>("Customer ID");
        colCust.setCellValueFactory(new PropertyValueFactory<>("idCustomer"));

        TableColumn<Order, Double> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        
        TableColumn<Order, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(colId, colCust, colTotal, colStatus);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Load Data
        table.getItems().setAll(adminController.getPendingOrders());

        Button btnAssign = new Button("Assign to Courier 🛵");
        Button btnRefresh = new Button("Refresh Data");

        btnAssign.setOnAction(e -> {
            Order selectedOrder = table.getSelectionModel().getSelectedItem();
            if (selectedOrder == null) {
                showAlert(Alert.AlertType.WARNING, "Pilih Order dulu!");
                return;
            }

            List<String> couriers = adminController.getCouriersList();
            if (couriers.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Gak ada kurir yang tersedia!");
                return;
            }

            ChoiceDialog<String> dialog = new ChoiceDialog<>(couriers.get(0), couriers);
            dialog.setTitle("Assign Order");
            dialog.setHeaderText("Pilih Kurir untuk order " + selectedOrder.getIdOrder());
            dialog.setContentText("Courier:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(selectedCourier -> {
                String status = adminController.assignToCourier(selectedOrder.getIdOrder(), selectedCourier);
                if (status.equals("Success")) {
                    showAlert(Alert.AlertType.INFORMATION, "Berhasil! Kurir meluncur.");
                    table.getItems().setAll(adminController.getPendingOrders()); // Refresh table
                }
            });
        });

        btnRefresh.setOnAction(e -> table.getItems().setAll(adminController.getPendingOrders()));

        HBox actions = new HBox(10, btnAssign, btnRefresh);
        actions.setPadding(new Insets(10));
        
        VBox layout = new VBox(10, new Label("List Orderan Pending:"), table, actions);
        layout.setPadding(new Insets(10));
        return layout;
    }

    // --- LOGIC TAB 2 (STOCK) ---
    private VBox createStockContent() {
        TableView<Product> table = new TableView<>();
        
        TableColumn<Product, String> colName = new TableColumn<>("Product Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        TableColumn<Product, Integer> colStock = new TableColumn<>("Current Stock");
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        
        TableColumn<Product, Double> colPrice = new TableColumn<>("Price");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        table.getColumns().addAll(colName, colStock, colPrice);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Load Data
        table.getItems().setAll(adminController.getAllProducts());
        
        Button btnUpdate = new Button("Update Stock ✏️");
        Button btnRefresh = new Button("Refresh");
        
        btnUpdate.setOnAction(e -> {
            Product selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "Pilih produk yang mau diupdate stoknya!");
                return;
            }
            
            TextInputDialog dialog = new TextInputDialog(String.valueOf(selected.getStock()));
            dialog.setTitle("Update Stock");
            dialog.setHeaderText("Update stok untuk: " + selected.getName());
            dialog.setContentText("Stok Baru:");
            
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(stockStr -> {
                if (!stockStr.matches("\\d+")) {
                    showAlert(Alert.AlertType.ERROR, "Stok harus angka valid!");
                    return;
                }
                
                int newStock = Integer.parseInt(stockStr);
                String status = adminController.updateStock(selected.getIdProduct(), newStock);
                
                if (status.equals("Success")) {
                    showAlert(Alert.AlertType.INFORMATION, "Stok berhasil diupdate!");
                    table.getItems().setAll(adminController.getAllProducts()); // Refresh table
                } else {
                    showAlert(Alert.AlertType.ERROR, status);
                }
            });
        });
        
        btnRefresh.setOnAction(e -> table.getItems().setAll(adminController.getAllProducts()));
        
        HBox actions = new HBox(10, btnUpdate, btnRefresh);
        actions.setPadding(new Insets(10));
        
        VBox layout = new VBox(10, new Label("Manajemen Stok Produk:"), table, actions);
        layout.setPadding(new Insets(10));
        return layout;
    }

    private void showAlert(Alert.AlertType type, String content) {
        Alert alert = new Alert(type);
        alert.setContentText(content);
        alert.show();
    }
}