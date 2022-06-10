package manage.staff;

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


import java.time.LocalDate;
import java.util.ResourceBundle;


public class StaffManager implements Initializable {


    @FXML
    private TextField txtID;
    @FXML
    private TextField txtFN;
    @FXML
    private TextField txtLN;
    @FXML
    private TextField txtOcc;
    @FXML
    private DatePicker DOB;
    @FXML
    private TextField txtUsername;
    @FXML
    private TextField txtPass;



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

    }

   @FXML
    private void HandleEvents(MouseEvent event) {
        //check if not empty
        if (txtID.getText().isEmpty() || txtFN.getText().isEmpty() || txtLN.getText().isEmpty() || txtOcc.getText().isEmpty()  || txtUsername.getText().isEmpty() || txtPass.getText().isEmpty()|| DOB.getValue().isEqual(null)) {
            lblStatus.setTextFill(Color.TOMATO);
            lblStatus.setText("Enter all details");
        } else {
            saveData();
        }

    }

    private void clearFields() {
        txtID.clear();
        txtFN.clear();
        txtLN.clear();
        txtOcc.clear();
        DOB.setValue(null);
        txtUsername.clear();
        txtPass.clear();



    }
    @FXML
    private String saveData() {

        try {
            String st = "INSERT INTO staff(staffID, staffOccupation, fName, lName, dateOfBirth, username, password) VALUES (?,?,?,?,?,?,?)";
            preparedStatement = (PreparedStatement) connection.prepareStatement(st);
            preparedStatement.setString(1, txtID.getText());
            preparedStatement.setString(2, txtOcc.getText());
            preparedStatement.setString(3, txtFN.getText());
            preparedStatement.setString(4, txtLN.getText());

            preparedStatement.setDate(5, Date.valueOf(DOB.getValue()));
            preparedStatement.setString(6, txtUsername.getText());
            preparedStatement.setString(7, txtPass.getText());


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
            String st1 = "DELETE FROM imports WHERE staffID=" + txtID.getText() + "";
            preparedStatement = (PreparedStatement) connection.prepareStatement(st1);
            preparedStatement.executeUpdate();

            String st = "DELETE FROM staff WHERE staffID=" + txtID.getText() + "";
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

        String st = "UPDATE staff SET staffID='"+txtID.getText()+"',fName='"+txtFN.getText()+"',lName='"+txtLN.getText()+"',dateOfBirth='" +Date.valueOf(DOB.getValue())+"' ,username='"+txtUsername.getText()+"',password='"+txtPass.getText()+"' WHERE staffID="+txtID.getText()+"";
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
    String SQL = "SELECT * from staff";

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
