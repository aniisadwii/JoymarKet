package view;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import util.Session;

public class AdminMainView {
    private Stage stage;

    public AdminMainView(Stage stage) {
        this.stage = stage;
        initialize();
    }

    private void initialize() {
        String name = Session.getInstance().getUser().getFullName();
        
        BorderPane root = new BorderPane();
        
        Label lblHeader = new Label("Admin Dashboard - Hello, " + name);
        lblHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10;");
        
        Button btnEditProfile = new Button("Edit Profile");
        btnEditProfile.setOnAction(e -> {
            new UserWindow(); 
        });
        
        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> {
            Session.getInstance().setUser(null);
            new LoginView(stage);
        });
        
        HBox topBar = new HBox(10, lblHeader, btnEditProfile, btnLogout);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: #ddd;");
        
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        Tab tabOrders = new Tab("Incoming Orders");
        DeliveryWindow deliveryWindow = new DeliveryWindow(); 
        tabOrders.setContent(deliveryWindow.getView());                   
        
        Tab tabStock = new Tab("Manage Stock");
        ProductStockWindow stockWindow = new ProductStockWindow();
        tabStock.setContent(stockWindow.getView());
        
        Tab tabCouriers = new Tab("Couriers");
        CourierWindow courierWindow = new CourierWindow();
        tabCouriers.setContent(courierWindow.getView());
        
        Tab tabAllOrders = new Tab("All Orders History");
        AllOrdersWindow allOrdersWindow = new AllOrdersWindow(); 
        tabAllOrders.setContent(allOrdersWindow.getView());
        
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == tabAllOrders) {
                allOrdersWindow.refresh();
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