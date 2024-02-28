package main.java.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.entitymanager.TransactionManager;
import main.java.models.CustomerSalesOrderTableModel;
import main.java.models.SalesOrder;
import main.java.models.SalesOrderTableModel;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Author: Afif Al Mamun
 * Written on: 8/1/2018
 * Project: TeslaRentalInventory
 **/
public class CustomerPurchaseListController implements Initializable {
    @FXML
    private TableView<CustomerSalesOrderTableModel> tblRecent;
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
    private Label lblDue, today;
    @FXML
    private Label lblAmount;
    public static int customerID = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        today.setText(LocalDate.now().toString());
        purID.setCellValueFactory(new PropertyValueFactory<>("id"));
        cusID.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        itemID.setCellValueFactory(new PropertyValueFactory<>("productName"));
        date.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        qty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        paidAmmount.setCellValueFactory(new PropertyValueFactory<>("price"));
        dueAmount.setCellValueFactory(new PropertyValueFactory<>("due"));
        empName.setCellValueFactory(new PropertyValueFactory<>("user"));

        try {
            List<SalesOrder> salesOrders = TransactionManager.getSalesOrdersByCustomerId(customerID);

            ObservableList<CustomerSalesOrderTableModel> list = FXCollections.observableArrayList();

            AtomicReference<Integer> ctr = new AtomicReference<>(0);
            AtomicReference<Double> due = new AtomicReference<Double>(0.0);
            AtomicReference<Double> total = new AtomicReference<>(0.0);

            salesOrders.forEach(salesOrder -> {
                list.addAll(new CustomerSalesOrderTableModel(salesOrder.getId(),
                        salesOrder.getCustomer().getName(),
                        salesOrder.getProducts().toString(),
                        String.valueOf(salesOrder.getOrderDate()),
                        salesOrder.getProducts().size(),
                        salesOrder.getTotalPrice()));

                ctr.getAndSet(ctr.get() + 1);
                due.set(Double.valueOf(0));
                total.set(salesOrder.getTotalPrice());
            });

            lblDue.setText(due.toString() + " $");
            lblAmount.setText(total.toString() + " $");
            lblSellCount.setText(ctr.toString());

            tblRecent.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
