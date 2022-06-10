package manage.livestock;

import java.awt.event.ActionEvent;
import java.sql.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import utils.ConnectionUtil;


import javax.swing.*;

import static utils.ConnectionUtil.conDB;

public class LivestockManager implements Initializable {
    ObservableList<String> list = FXCollections.observableArrayList();


    @FXML
    private TextField txtID;
    @FXML
    private TextField txtType;
    @FXML
    private TextField txtHS;
    @FXML
    private TextField txtAge;
    @FXML
    Label lblStatus;
    @FXML
    TableView tblData;

    @FXML
   private BarChart<String,Double>barChart;
    @FXML
    private JFXButton btnLoad;

   XYChart.Series<String, Double> livestockChart;




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


        }



   @FXML
    private void HandleEvents() {
        //check if not empty
        if (txtID.getText().isEmpty() || txtType.getText().isEmpty() || txtHS.getText().isEmpty() || txtAge.getText().equals(null)) {
            lblStatus.setTextFill(Color.TOMATO);
            lblStatus.setText("Enter all details");
        } else {
            saveData();
        }

    }

    private void clearFields() {
        txtID.clear();
        txtType.clear();
        txtHS.clear();
        txtAge.clear();
    }

    public void loadChart () {
       String query = "SELECT livestocktype, COUNT(*) AS NumberOfLivestock from livestock group by livestocktype;";
        barChart.getData().clear();
        barChart.layout();
        livestockChart = new XYChart.Series<>();

        try{
            connection=conDB();
            ResultSet rs = connection.createStatement().executeQuery(query);
            while(rs.next()) {

                livestockChart.getData().add(new XYChart.Data<>(rs.getString(1), rs.getDouble(2)));

            }
             barChart.getData().add(livestockChart);


        }catch(Exception e){

        }
        }


    private String saveData() {

        try {
            String st = "INSERT INTO livestock(livestockID, livestockType, healthStatus, age) VALUES (?,?,?,?)";
            preparedStatement = (PreparedStatement) connection.prepareStatement(st);
            preparedStatement.setString(1, txtID.getText());
            preparedStatement.setString(2, txtType.getText());
            preparedStatement.setString(3, txtHS.getText());
            preparedStatement.setString(4, txtAge.getText());


            preparedStatement.executeUpdate();
            lblStatus.setTextFill(Color.GREEN);
            lblStatus.setText("Added Successfully");

            fetRowList();
            //clear fields
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
    private String deleteData() {
        try {
            String st = "DELETE FROM livestock WHERE livestockID=" + txtID.getText() + "";
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
        String st = "UPDATE livestock SET livestockID='"+txtID.getText()+"',livestockType='"+txtType.getText()+"',healthstatus='"+txtHS.getText()+"',Age='"+txtAge.getText()+"' WHERE livestockID="+txtID.getText()+"";
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



    private ObservableList<ObservableList> data;
    String SQL = "SELECT * from livestock";

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
