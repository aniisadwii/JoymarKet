package view;

import controller.DeliveryHandler;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.OrderHeader;
import util.Session;
import java.util.*;

// class ini untuk mengatur tampilan manajemen pengiriman, digunakan oleh admin (assign kurir) dan kurir (update status)
public class DeliveryWindow {
    
    private DeliveryHandler deliveryHandler = new DeliveryHandler();
    private VBox layout;
    private TableView<OrderHeader> table;
    
    // variabel global untuk komponen ui agar bisa diakses saat refresh
    private Label lblEmpty; 
    private Label lblTitle;
    private HBox actions; 
    
    private String userRole;
    private String userId;

    // constructor ini untuk inisialisasi window delivery dan mengambil data user dari session
    public DeliveryWindow() {
        this.userRole = Session.getInstance().getUser().getRole();
        this.userId = Session.getInstance().getUser().getIdUser();
        initialize();
    }

    // method ini untuk menyusun layout, tabel, dan tombol aksi berdasarkan role user yang login
    private void initialize() {
        layout = new VBox(10);
        layout.setPadding(new Insets(10));

        // 1. setup judul dinamis
        String title = userRole.equals("Admin") ? "Incoming Orders (Pending)" : "My Assigned Deliveries";
        lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // 2. setup label pesan jika data kosong
        lblEmpty = new Label(userRole.equals("Admin") ? "No Pending Orders" : "No Assigned Delivery Available");
        lblEmpty.setStyle("-fx-text-fill: grey; -fx-font-size: 16px;");

        // 3. setup struktur tabel
        setupTable();
        
        // 4. setup tombol aksi (assign/update/refresh)
        actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_LEFT);

        if (userRole.equals("Admin")) {
            // tombol khusus admin untuk assign order
            Button btnAssign = new Button("Assign to Courier");
            btnAssign.setOnAction(e -> handleAssignCourier());
            actions.getChildren().add(btnAssign);
            
        } else if (userRole.equals("Courier")) {
            // tombol khusus kurir untuk update status
            Button btnUpdateStatus = new Button("Update Status");
            btnUpdateStatus.setOnAction(e -> handleUpdateStatus());
            actions.getChildren().add(btnUpdateStatus);
        }

        Button btnRefresh = new Button("Refresh");
        btnRefresh.setOnAction(e -> refreshTable());
        actions.getChildren().add(btnRefresh);

        // 5. panggil refresh untuk memuat data awal
        refreshTable();
    }
    
    // method ini untuk konfigurasi kolom-kolom pada tabel order
    private void setupTable() {
        table = new TableView<>();
        
        TableColumn<OrderHeader, String> colId = new TableColumn<>("Order ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idOrder"));
        
        TableColumn<OrderHeader, String> colCust = new TableColumn<>("Customer ID");
        colCust.setCellValueFactory(new PropertyValueFactory<>("idCustomer"));
        
        TableColumn<OrderHeader, Double> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        
        TableColumn<OrderHeader, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(colId, colCust, colTotal, colStatus);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // method ini untuk mengambil data terbaru dan memperbarui tampilan tabel
    private void refreshTable() {
        List<OrderHeader> data;
        
        // fetch data sesuai role (admin: pending orders, kurir: my jobs)
        if (userRole.equals("Admin")) {
            data = deliveryHandler.getPendingOrders();
        } else {
            data = deliveryHandler.getDeliveries(userId);
        }
        
        // bersihkan layout sebelum diisi ulang
        layout.getChildren().clear();
        
        // logika decision node: tampilkan tabel jika ada data, atau pesan kosong jika tidak
        if (data.isEmpty()) {
            layout.getChildren().addAll(lblTitle, lblEmpty, actions);
        } else {
            table.getItems().setAll(data);
            layout.getChildren().addAll(lblTitle, table, actions);
        }
    }

    // method ini untuk menangani aksi admin saat menugaskan kurir ke order tertentu
    private void handleAssignCourier() {
        OrderHeader selectedOrder = table.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showAlert(Alert.AlertType.WARNING, "Please select an Order first");
            return;
        }

        List<String> couriers = deliveryHandler.getCouriersList();
        if (couriers.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "No couriers available");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(couriers.get(0), couriers);
        dialog.setTitle("Assign Order");
        dialog.setHeaderText("Select Courier for Order " + selectedOrder.getIdOrder());
        dialog.setContentText("Courier:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(selectedCourierString -> {
            String idCourier = selectedCourierString.split(" - ")[0];
            String status = deliveryHandler.assignCourierToOrder(selectedOrder.getIdOrder(), idCourier);
            
            if (status.equals("Success")) {
                showAlert(Alert.AlertType.INFORMATION, "Success! Courier assigned.");
                refreshTable(); 
            }
        });
    }

    // method ini untuk menangani aksi kurir saat memperbarui status pengiriman
    private void handleUpdateStatus() {
        OrderHeader selectedJob = table.getSelectionModel().getSelectedItem();
        if (selectedJob == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a job first");
            return;
        }

        List<String> statusOptions = List.of("Pending", "In Progress", "Delivered");

        ChoiceDialog<String> dialog = new ChoiceDialog<>(selectedJob.getStatus(), statusOptions);
        dialog.setTitle("Update Delivery Status");
        dialog.setHeaderText("Update status for Order: " + selectedJob.getIdOrder());
        dialog.setContentText("Select New Status:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newStatus -> {
        	String updateResult = deliveryHandler.editDeliveryStatus(selectedJob.getIdOrder(), userId, newStatus);
            
            if (updateResult.equals("Success")) {
                showAlert(Alert.AlertType.INFORMATION, "Status updated successfully to: " + newStatus);
                refreshTable(); 
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to update status");
            }
        });
    }

    // method ini untuk mengembalikan layout utama agar bisa ditampilkan di scene
    public VBox getView() {
        return layout;
    }

    private void showAlert(Alert.AlertType type, String content) {
        Alert alert = new Alert(type);
        alert.setContentText(content);
        alert.show();
    }
}