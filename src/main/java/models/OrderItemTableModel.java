package main.java.models;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class OrderItemTableModel {
    private String id;
    private String productName;
    private String productModel;
    private int perPeicePrice;
    private int qty;
}
