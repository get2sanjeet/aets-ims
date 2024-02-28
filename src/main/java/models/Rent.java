package main.java.models;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Author: Afif Al Mamun
 * Written on: 7/29/2018
 * Project: TeslaRentalInventory
 **/
@Data
@AllArgsConstructor
public class Rent {
    int rentID;
    int itemID;
    int cusID;
    String rentDate;
    String returnDate;
    double payAmount, amountDue;
    String user;

    public Rent(int rentID, int itemID, int cusID, String rentDate, String returnDate, double payAmount, double amountDue) {
        this.rentID = rentID;
        this.itemID = itemID;
        this.cusID = cusID;
        this.rentDate = rentDate;
        this.returnDate = returnDate;
        this.payAmount = payAmount;
        this.amountDue = amountDue;
    }
}
