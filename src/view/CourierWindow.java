package view;

import controller.CourierHandler;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.Courier;

public class CourierWindow {
    
    private CourierHandler courierHandler = new CourierHandler();
    private VBox layout;

    public CourierWindow() {
        initialize();
    }

    private void initialize() {
        layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label lblTitle = new Label("Courier List 🛵");
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // --- Setup Tabel (Sama kayak sebelumnya) ---
        TableView<Courier> table = new TableView<>();
        
        TableColumn<Courier, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        
        TableColumn<Courier, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        
        TableColumn<Courier, String> colPhone = new TableColumn<>("Phone");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        
        TableColumn<Courier, String> colVehicle = new TableColumn<>("Vehicle");
        colVehicle.setCellValueFactory(new PropertyValueFactory<>("vehicleType"));
        
        TableColumn<Courier, String> colPlate = new TableColumn<>("Plate Number");
        colPlate.setCellValueFactory(new PropertyValueFactory<>("vehiclePlate"));

        table.getColumns().addAll(colId, colName, colPhone, colVehicle, colPlate);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Load Data Awal (Pakai getAllCouriers)
        refreshTable(table);
        
        // --- TOMBOL BARU: VIEW DETAILS ---
        Button btnDetail = new Button("View Details");
        Button btnRefresh = new Button("Refresh");

        // Logic Tombol View Details
        // Di sinilah method getCourier(id) dipanggil!
        btnDetail.setOnAction(e -> {
            Courier selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "Please select a courier first");
                return;
            }

            // 1. Panggil Controller buat ambil data spesifik (Fresh from DB)
            // Ini ngebuktiin method getCourier kepake!
            Courier detail = courierHandler.getCourier(selected.getIdUser());
            
            if (detail != null) {
                // 2. Tampilkan Info Lengkap
                String info = "Courier Profile:\n\n" +
                              "ID: " + detail.getIdUser() + "\n" +
                              "Name: " + detail.getFullName() + "\n" +
                              "Email: " + detail.getEmail() + "\n" +
                              "Phone: " + detail.getPhone() + "\n" +
                              "Address: " + detail.getAddress() + "\n" +
                              "--------------------------\n" +
                              "Vehicle: " + detail.getVehicleType() + "\n" +
                              "Plate: " + detail.getVehiclePlate();
                              
                showAlert(Alert.AlertType.INFORMATION, "Courier Details", info);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to fetch data.");
            }
        });

        btnRefresh.setOnAction(e -> refreshTable(table));

        HBox actions = new HBox(10, btnDetail, btnRefresh);
        layout.getChildren().addAll(lblTitle, table, actions);
    }

    public VBox getView() {
        return layout;
    }
    
    private void refreshTable(TableView<Courier> table) {
        table.getItems().setAll(courierHandler.getAllCouriers());
    }
    
    // Method overloading biar praktis
    private void showAlert(Alert.AlertType type, String content) {
        showAlert(type, "Message", content);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}