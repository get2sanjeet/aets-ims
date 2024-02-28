package main.java.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.models.DBConnection;
import main.java.models.SalesOrderTableModel;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Author: Afif Al Mamun
 * Written on: 8/1/2018
 * Project: TeslaRentalInventory
 **/
public class SellListController implements Initializable {
    @FXML
    private TableView<SalesOrderTableModel> tblRecent;
    @FXML
    private TableColumn<SalesOrderTableModel, Integer> purID;
    @FXML
    private TableColumn<SalesOrderTableModel, Integer> cusID;
    @FXML
    private TableColumn<SalesOrderTableModel, Integer> itemID;
    @FXML
    private TableColumn<SalesOrderTableModel, String> date;
    @FXML
    private TableColumn<SalesOrderTableModel, Integer> qty;
    @FXML
    private TableColumn<SalesOrderTableModel, Double> paidAmmount;
    @FXML
    private TableColumn<SalesOrderTableModel, Double> dueAmount;
    @FXML
    private TableColumn<SalesOrderTableModel, String> empName;
    @FXML
    private Label lblSellCount;
    @FXML
    private Label lblHeader, lblDue, today;
    @FXML
    private Label lblAmount;
    public static boolean todayFlag = false;
    PreparedStatement getSellsList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Connection con = DBConnection.getConnection();

        if(todayFlag) {
            lblHeader.setText("Today's Sells Report");
            try {
                getSellsList = con.prepareStatement("SELECT * FROM purchases WHERE purchaseDate ='"+ Date.valueOf(LocalDate.now()) +"'");
                showReport();
                todayFlag = false;
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                getSellsList = con.prepareStatement("SELECT * FROM purchases");
                showReport();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void showReport() {
        today.setText(LocalDate.now().toString());
        purID.setCellValueFactory(new PropertyValueFactory<>("purID"));
        cusID.setCellValueFactory(new PropertyValueFactory<>("cusID"));
        itemID.setCellValueFactory(new PropertyValueFactory<>("itemID"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        qty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        paidAmmount.setCellValueFactory(new PropertyValueFactory<>("paid"));
        dueAmount.setCellValueFactory(new PropertyValueFactory<>("due"));
        empName.setCellValueFactory(new PropertyValueFactory<>("user"));

        try {
            ResultSet sellsList = getSellsList.executeQuery();

            ObservableList<SalesOrderTableModel> list = FXCollections.observableArrayList();

            Integer ctr = 0;
            Double due = 0.0;
            Double total = 0.0;

            while(sellsList.next()) {
                list.add(new SalesOrderTableModel(sellsList.getString("purchaseID"),
                        sellsList.getString("Customers_customerID"),
                        sellsList.getString("Item_itemID"),
                        sellsList.getString("purchaseDate"),
                        sellsList.getInt("purchaseQuantity"),
                        sellsList.getDouble("payAmount")));

                ctr++;
                due += sellsList.getDouble("amountDue");
                total += sellsList.getDouble("payAmount");

            }

            lblDue.setText(due.toString() + " $");
            lblAmount.setText(total.toString() + " $");
            lblSellCount.setText(ctr.toString());

            tblRecent.setItems(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
