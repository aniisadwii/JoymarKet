package view;

import controller.DeliveryHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Order;
import util.Session;
import java.util.List;
import java.util.Optional;

public class CourierMainView {
    private Stage stage;
    private DeliveryHandler deliveryHandler = new DeliveryHandler();

    public CourierMainView(Stage stage) {
        this.stage = stage;
        initialize();
    }

    private void initialize() {
        String courierName = Session.getInstance().getUser().getFullName();
        String courierId = Session.getInstance().getUser().getIdUser();

        Label lblTitle = new Label("Dashboard Kurir - " + courierName);
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // 1. Tabel Pekerjaan
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

        // Load Data Awal
        refreshTable(table, courierId);

        // 2. Tombol Aksi (Refresh Dihapus)
        Button btnUpdateStatus = new Button("Update Status 📝");
        Button btnEditProfile = new Button("Edit Profile ✏️");
        btnEditProfile.setOnAction(e -> {
            new UserWindow(); 
        });
        Button btnLogout = new Button("Logout");

        // Logic Update Status
        btnUpdateStatus.setOnAction(e -> {
            Order selectedJob = table.getSelectionModel().getSelectedItem();
            if (selectedJob == null) {
                showAlert(Alert.AlertType.WARNING, "Pilih kerjaan dulu bang kurir!");
                return;
            }

            List<String> statusOptions = List.of("Pending", "In Progress", "Delivered");

            ChoiceDialog<String> dialog = new ChoiceDialog<>(selectedJob.getStatus(), statusOptions);
            dialog.setTitle("Update Delivery Status");
            dialog.setHeaderText("Update status untuk Order: " + selectedJob.getIdOrder());
            dialog.setContentText("Pilih Status Baru:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(newStatus -> {
                // Panggil Controller
                String updateResult = deliveryHandler.editDeliveryStatus(selectedJob.getIdOrder(), newStatus);
                
                if (updateResult.equals("Success")) {
                    showAlert(Alert.AlertType.INFORMATION, "Status berhasil diupdate jadi: " + newStatus);
                    refreshTable(table, courierId); // <--- INI SUDAH CUKUP BUAT REFRESH
                } else {
                    showAlert(Alert.AlertType.ERROR, "Gagal update status!");
                }
            });
        });

        btnLogout.setOnAction(e -> {
            Session.getInstance().setUser(null);
            new LoginView(stage);
        });

        // Layouting
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        // Hapus btnRefresh dari sini
        layout.getChildren().addAll(lblTitle, new Label("List Pengiriman Aktif:"), table, btnUpdateStatus, btnEditProfile, btnLogout);

        BorderPane root = new BorderPane();
        root.setCenter(layout);

        Scene scene = new Scene(root, 600, 500);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Courier App");
        stage.show();
    }

    private void refreshTable(TableView<Order> table, String courierId) {
        table.getItems().setAll(deliveryHandler.getDeliveries(courierId));
    }

    private void showAlert(Alert.AlertType type, String content) {
        Alert alert = new Alert(type);
        alert.setContentText(content);
        alert.show();
    }
}