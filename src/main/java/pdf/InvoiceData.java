package main.java.pdf;

import lombok.Data;

@Data
public final class InvoiceData {
    private final int serial;
    private final String productName;
    private final int quantity;
    private final float unitPrice;
    private final float totalPrice;

    InvoiceData(int serial, String productName, int quantity, float unitPrice) {
        this.serial = serial;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = quantity * unitPrice;
    }
}
