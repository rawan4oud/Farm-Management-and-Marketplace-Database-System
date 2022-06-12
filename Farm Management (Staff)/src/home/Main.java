package home;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.event.*;
import javafx.stage.StageStyle;
import manage.crops.CropsManager;
import manage.deliveries.DeliveriesManager;
import manage.inventory.InventoryManager;
import manage.livestock.LivestockManager;
import manage.staff.StaffManager;
import manage.supplies.SuppliesManager;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main extends Application {

    StaffManager sm = new StaffManager();
    CropsManager cm = new CropsManager();
    LivestockManager lm = new LivestockManager();
    SuppliesManager supm = new SuppliesManager();
    InventoryManager im = new InventoryManager();
    DeliveriesManager dm = new DeliveriesManager();


    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/login/login.fxml"));

        stage.initStyle(StageStyle.DECORATED);
        stage.setMaximized(false);


        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });



       root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    private Stage stage;
    private Scene scene;
    private Parent root;

    public static void main(String[] args) {
        launch(args);

    }


    @FXML
    private void handleButtonStaff(MouseEvent mouseEvent) throws IOException, SQLException {
        loadWindow(getClass().getResource("/manage/staff/managestaff.fxml"), "Staff Manager", null);
        sm.closeCon();
    }

    @FXML
    private void handleButtonCrops(MouseEvent mouseEvent) throws IOException, SQLException {
        loadWindow(getClass().getResource("/manage/crops/managecrops.fxml"), "Crops Manager", null);

        cm.closeCon();
        System.out.println("Here");
    }

    @FXML
    private void handleButtonLivestock(MouseEvent mouseEvent) throws IOException, SQLException {
        loadWindow(getClass().getResource("/manage/livestock/managelivestock.fxml"), "Livestock Manager", null);
        lm.closeCon();
    }

    @FXML
    private void handleButtonSupplies(MouseEvent mouseEvent) throws IOException, SQLException {
        loadWindow(getClass().getResource("/manage/supplies/managesupplies.fxml"), "Supplies Manager", null);
        supm.closeCon();
    }

    @FXML
    private void handleButtonInventory(MouseEvent mouseEvent) throws IOException, SQLException {
        loadWindow(getClass().getResource("/manage/inventory/manageinventory.fxml"), "Inventory", null);
        im.closeCon();
    }

    @FXML
    private void handleButtonDeliveries(MouseEvent mouseEvent) throws IOException, SQLException {
        loadWindow(getClass().getResource("/manage/deliveries/managedeliveries.fxml"), "Deliveries", null);
        dm.closeCon();
    }

    public static Object loadWindow(URL loc, String title, Stage parentStage) throws IOException {
        Object controller = null;

            FXMLLoader loader = new FXMLLoader(loc);
            Parent parent = loader.load();
            controller = loader.getController();
            Stage stage = null;
            if (parentStage != null) {
                stage = parentStage;
            } else {
                stage = new Stage(StageStyle.DECORATED);
            }
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            stage.show();

        return controller;
    }
}
