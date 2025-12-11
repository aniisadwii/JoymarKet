package view;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import util.Session;

public class AdminMainView {
    private Stage stage;

    // constructor ini untuk inisialisasi view dashboard admin
    public AdminMainView(Stage stage) {
        this.stage = stage;
        initialize();
    }

    // method ini untuk menyusun tata letak dan inisialisasi seluruh komponen ui dashboard
    private void initialize() {
        // ambil nama admin yang sedang login dari session
        String name = Session.getInstance().getUser().getFullName();
        
        BorderPane root = new BorderPane();
        
        Label lblHeader = new Label("Admin Dashboard - Hello, " + name);
        lblHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10;");
        
        // tombol ini untuk membuka window edit profil
        Button btnEditProfile = new Button("Edit Profile");
        btnEditProfile.setOnAction(e -> {
            new UserWindow(); 
        });
        
        // tombol ini untuk proses logout, hapus sesi, dan kembali ke login
        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> {
            Session.getInstance().setUser(null);
            new LoginView(stage);
        });
        
        HBox topBar = new HBox(10, lblHeader, btnEditProfile, btnLogout);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: #ddd;");
        
        // tabpane ini untuk menampung berbagai fitur admin dalam tab terpisah
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        // tab ini untuk fitur assign kurir ke pesanan yang masuk (menggunakan deliverywindow)
        Tab tabOrders = new Tab("Incoming Orders");
        DeliveryWindow deliveryWindow = new DeliveryWindow(); 
        tabOrders.setContent(deliveryWindow.getView());                   
        
        // tab ini untuk manajemen stok produk (menggunakan productwindow mode admin)
        Tab tabStock = new Tab("Manage Stock");
        ProductWindow productWindow = new ProductWindow();
        tabStock.setContent(productWindow.getView());
        
        // tab ini untuk melihat daftar kurir yang terdaftar
        Tab tabCouriers = new Tab("Couriers");
        CourierWindow courierWindow = new CourierWindow();
        tabCouriers.setContent(courierWindow.getView());
        
        // tab ini untuk melihat rekap seluruh pesanan yang pernah terjadi
        Tab tabAllOrders = new Tab("All Orders History");
        OrderHeaderWindow allOrdersView = new OrderHeaderWindow();
        tabAllOrders.setContent(allOrdersView.getView());
        
        // listener ini untuk refresh data tabel secara otomatis saat tab history dibuka
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == tabAllOrders) {
            	allOrdersView.refreshData();
            }
        });
        
        tabPane.getTabs().addAll(tabOrders, tabStock, tabCouriers, tabAllOrders);
        
        root.setTop(topBar);
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Admin Panel");
        stage.show();
    }
}