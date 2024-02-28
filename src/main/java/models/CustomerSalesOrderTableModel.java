package main.java.models;

import com.jfoenix.controls.JFXButton;
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
public class CustomerSalesOrderTableModel {
    int id;
    String customerName;
    String productName;
    String orderDate;
    int qty;
    double price;
}
