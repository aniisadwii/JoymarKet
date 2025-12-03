package view;

import controller.CourierController;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Order;
import util.Session;

public class CourierMainView {
    private Stage stage;
    private CourierController courierController = new CourierController();

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

        TableColumn<Order, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(colId, colCust, colStatus);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Load Data
        refreshTable(table, courierId);

        // 2. Tombol Aksi
        Button btnFinish = new Button("Selesaikan Pengiriman ✅");
        Button btnRefresh = new Button("Refresh");
        Button btnLogout = new Button("Logout");

        // Logic Selesaikan Pengiriman
        btnFinish.setOnAction(e -> {
            Order selectedJob = table.getSelectionModel().getSelectedItem();
            if (selectedJob == null) {
                showAlert(Alert.AlertType.WARNING, "Pilih kerjaan dulu bang kurir!");
                return;
            }

            // Konfirmasi
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Paket udah sampe ke customer?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait();

            if (confirm.getResult() == ButtonType.YES) {
                String status = courierController.completeDelivery(selectedJob.getIdOrder());
                if (status.equals("Success")) {
                    showAlert(Alert.AlertType.INFORMATION, "Mantap! Paket Delivered.");
                    refreshTable(table, courierId);
                }
            }
        });

        btnRefresh.setOnAction(e -> refreshTable(table, courierId));

        btnLogout.setOnAction(e -> {
            Session.getInstance().setUser(null);
            new LoginView(stage);
        });

        // Layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(lblTitle, new Label("List Pengiriman Aktif:"), table, btnFinish, btnRefresh, btnLogout);

        BorderPane root = new BorderPane();
        root.setCenter(layout);

        Scene scene = new Scene(root, 600, 500);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Courier App");
        stage.show();
    }

    private void refreshTable(TableView<Order> table, String courierId) {
        table.getItems().setAll(courierController.getMyJobs(courierId));
    }

    private void showAlert(Alert.AlertType type, String content) {
        Alert alert = new Alert(type);
        alert.setContentText(content);
        alert.show();
    }
}