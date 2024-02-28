package main.java.models;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.scene.layout.HBox;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Author: Afif Al Mamun
 * Written on: 7/28/2018
 * Project: TeslaRentalInventory
 **/
@Data
@AllArgsConstructor
public class SalesOrderTableModel {
    String orderID;
    public HBox status;
    String customerName;
    String productName;
    String orderDate;
    int qty;
    double price;
    String user;
    JFXButton billDownload;

    public SalesOrderTableModel(String orderID, HBox status, String customerName, String productName, String orderDate, int qty, double price, JFXButton billDownload) {
        this.orderID = orderID;
        this.status = status;
        this.customerName = customerName;
        this.productName = productName;
        this.orderDate = orderDate;
        this.qty = qty;
        this.price = price;
        this.billDownload = billDownload;
    }
    public SalesOrderTableModel(String orderID, String customerName, String productName, String orderDate, int qty, double price) {
        this.orderID = orderID;
        this.customerName = customerName;
        this.productName = productName;
        this.orderDate = orderDate;
        this.qty = qty;
        this.price = price;
    }
}
