package main.java.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItem {
    int serial;
    String name;
    String model;
    int qty;
    float unitPrice;
    float totalPrice;

    public OrderItem(int serial, String name, String model, int qty, float unitPrice) {
        this.serial = serial;
        this.name = name;
        this.model = model;
        this.qty = qty;
        this.unitPrice = unitPrice;
        this.totalPrice = unitPrice * qty;
    }
}
