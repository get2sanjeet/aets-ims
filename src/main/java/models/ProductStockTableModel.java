package main.java.models;

import javafx.scene.layout.HBox;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductStockTableModel {
    public int id;
    public String productName;
    public String productModel;
    public int stockCount;
    public int price;
}
