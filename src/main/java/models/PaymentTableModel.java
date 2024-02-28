package main.java.models;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PaymentTableModel {
    private int id;
    private String product;
    private int qty;
    private double price;
}
