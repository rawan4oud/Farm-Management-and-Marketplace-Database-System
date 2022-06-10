package manage.supplies;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import utils.ConnectionUtil;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;


import java.util.ResourceBundle;


public class SuppliesManager implements Initializable {

    ObservableList<String> typeIDList = FXCollections.observableArrayList("Machinery", "Hand Tools", "Structure", "Vehicles", "Agrochemicals", "Feeding and Watering");


    @FXML
    private TextField staffID;
    @FXML
    private TextField supplyID;
    @FXML
    private ComboBox typeID;
    @FXML
    private DatePicker DOI;
    @FXML
    private TextField priceID;

    @FXML
    Label lblStatus;

    @FXML
    TableView tblData;


    PreparedStatement preparedStatement;
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

        if (staffID.getText().isEmpty() || supplyID.getText().isEmpty() || DOI.getValue().isEqual(null) || priceID.getText().equals(null)) {
            lblStatus.setTextFill(Color.TOMATO);
            lblStatus.setText("Enter all details");
        } else {
            saveData();
        }

    }

    private void clearFields() {
        staffID.clear();
        supplyID.clear();
        DOI.setValue(null);
        typeID.setValue(null);
        priceID.clear();
        //priceID.setValueFactory(null);


    }
    @FXML
    private String saveData() {

        try {

            String st1 = "INSERT INTO supplies(supplyID, importType) VALUES ('"+supplyID.getText()+"' ,'"+typeID.getValue().toString()+"')";
            PreparedStatement preparedStatement2 = (PreparedStatement) connection.prepareStatement(st1);
           preparedStatement2.executeUpdate();

            String st = "INSERT INTO  imports(staffID, supplyID, price, dateOfImport) VALUES ('"+staffID.getText()+"','"+supplyID.getText()+"','"+priceID.getText()+"','" +DOI.getValue()+"')";
            preparedStatement = (PreparedStatement) connection.prepareStatement(st);
            preparedStatement.executeUpdate();



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
            String st = "DELETE FROM imports WHERE supplyID='"+supplyID.getText()+"'";
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
            String st2 = "UPDATE supplies set supplyID = '"+supplyID.getText()+"' ,importType='"+typeID.getValue().toString()+"' WHERE supplyID='"+supplyID.getText()+"'";
            PreparedStatement preparedStatement2 = (PreparedStatement) connection.prepareStatement(st2);
            preparedStatement2.executeUpdate();

            String st = "UPDATE imports SET staffID='"+staffID.getText()+"', supplyID ='"+supplyID.getText()+"', price='"+priceID.getText()+"',dateOfImport='" +Date.valueOf(DOI.getValue())+"'  WHERE supplyID='"+supplyID.getText()+"'";

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
    String SQL = "SELECT * from supplies natural join imports";

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
