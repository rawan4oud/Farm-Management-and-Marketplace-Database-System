package manage.inventory;

import java.sql.*;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import utils.ConnectionUtil;


import static utils.ConnectionUtil.conDB;

public class InventoryManager implements Initializable {
    ObservableList<String> typeIDList = FXCollections.observableArrayList("Beef", "Pork", "Chicken", "Turkey", "Lamb", "Eggs", "Milk", "Wheat", "Rice", "Beans", "Soybeans", "Corn", "Tomatoes");


    @FXML
    private TextField txtID;

    @FXML
    private ComboBox typeID;

    @FXML
    private TextField priceID;

    @FXML
    Label lblStatus;

    @FXML
    TableView tblData;

    @FXML
    private BarChart<String,Double>barChart;
    @FXML
    private JFXButton btnLoad;

    XYChart.Series<String, Double> inventoryChart;


    PreparedStatement preparedStatement;
    ResultSet resultSet;
    Connection connection = null;
    private ConnectionUtil cu;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        cu = new ConnectionUtil();
        System.out.println("connected test1");
        try {
            connection = cu.conDB();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        fetColumnList();
        fetRowList();
        typeID.setItems(typeIDList);

    }

    @FXML
    private void HandleEvents(MouseEvent event) {
        //check if not empty
        if (txtID.getText().isEmpty() || priceID.getText().isEmpty()) {
            lblStatus.setTextFill(Color.TOMATO);
            lblStatus.setText("Enter all details");
        } else {
            saveData();
        }

    }

    private void clearFields() {
        txtID.clear();
        priceID.clear();
        typeID.setValue(null);



    }
    @FXML
    private String saveData() {

        try {

            String st1 = "INSERT INTO product(productID, productType) VALUES ('"+txtID.getText()+"' ,'"+typeID.getValue().toString()+"')";
            PreparedStatement preparedStatement2 = (PreparedStatement) connection.prepareStatement(st1);
            preparedStatement2.executeUpdate();

            String st = "INSERT INTO  productPrice(productID, productPrice) VALUES ('"+txtID.getText()+"','"+priceID.getText()+"')";
            preparedStatement = (PreparedStatement) connection.prepareStatement(st);
            preparedStatement.executeUpdate();


            lblStatus.setTextFill(Color.GREEN);
            lblStatus.setText("Added Successfully");

            fetRowList();

            clearFields();
            return "Success";

        } catch (SQLException ex) {
            return null;

        }
    }
    @FXML
    private String deleteData() {
        try {

            String st1 = "DELETE FROM productPrice WHERE productID='"+txtID.getText()+"'";
            PreparedStatement preparedStatement1 = (PreparedStatement) connection.prepareStatement(st1);
            preparedStatement1.executeUpdate();

            String st = "DELETE FROM product WHERE productID='"+txtID.getText()+"'";
            preparedStatement = (PreparedStatement) connection.prepareStatement(st);
            preparedStatement.executeUpdate();


            lblStatus.setTextFill(Color.GREEN);
            lblStatus.setText("Deleted Successfully");

            fetRowList();
            clearFields();
            return "Success";


        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            lblStatus.setTextFill(Color.TOMATO);
            lblStatus.setText(ex.getMessage());
            return "Exception";
        }
    }

    @FXML
    private String updateData(){
        try{
            String st2 = "UPDATE product set productID = '"+txtID.getText()+"' ,productType='"+typeID.getValue().toString()+"' WHERE productID='"+txtID.getText()+"'";
            PreparedStatement preparedStatement2 = (PreparedStatement) connection.prepareStatement(st2);
            preparedStatement2.executeUpdate();

            String st = "UPDATE productPrice SET productID='"+txtID.getText()+"', productPrice='"+priceID.getText()+"' WHERE productID='"+txtID.getText()+"'";

            preparedStatement = (PreparedStatement) connection.prepareStatement(st);
            preparedStatement.executeUpdate();


            lblStatus.setTextFill(Color.GREEN);
            lblStatus.setText("Updated Successfully");

            fetRowList();
            clearFields();
            return "Success";


        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            lblStatus.setTextFill(Color.TOMATO);
            lblStatus.setText(ex.getMessage());
            return "Exception";
        }
    }

    public void loadChart () {
        String query = "SELECT productType, COUNT(*) AS NumberOfProducts from product group by productType;";
        barChart.getData().clear();
        barChart.layout();
        inventoryChart = new XYChart.Series<>();

        try{
            connection=conDB();
            ResultSet rs = connection.createStatement().executeQuery(query);
            while(rs.next()) {

                inventoryChart.getData().add(new XYChart.Data<>(rs.getString(1), rs.getDouble(2)));

            }
            barChart.getData().add(inventoryChart);


        }catch(Exception e){

        }
    }

    private ObservableList<ObservableList> data;
    String SQL = "SELECT * from product natural join productPrice ";

    //only fetch columns
    private void fetColumnList() {

        try {
            ResultSet rs = connection.createStatement().executeQuery(SQL);


            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {

                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1).toUpperCase());
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });

                tblData.getColumns().removeAll(col);
                tblData.getColumns().addAll(col);

                System.out.println("Column [" + i + "] ");

            }

        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());

        }
    }

    //fetches rows and data from the list
    private void fetRowList() {
        data = FXCollections.observableArrayList();
        ResultSet rs;
        try {
            rs = connection.createStatement().executeQuery(SQL);

            while (rs.next()) {
                //Iterate Row
                ObservableList row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(rs.getString(i));
                }
                System.out.println("Row [1] added " + row);
                data.add(row);

            }

            tblData.setItems(data);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
    public void closeCon() throws SQLException {
        connection.close();
    }

}
