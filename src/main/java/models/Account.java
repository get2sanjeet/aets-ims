package main.java.models;

import lombok.Data;

/**
 * Author: Afif Al Mamun
 * Written on: 7/30/2018
 * Project: TeslaRentalInventory
 **/
@Data
public class Account {
    int accID;
    String cusName;
    String accName;
    int cusID;
    String paymethod;
    String user;

    public Account(int accID, String cusName, String accName, String paymethod) {
        this.accID = accID;
        this.cusName = cusName;
        this.accName = accName;
        this.paymethod = paymethod;
    }

    public Account(int accID, String accName, int cusID, String user, String paymethod) {
        this.accID = accID;
        this.accName = accName;
        this.cusID = cusID;
        this.user = user;
        this.paymethod = paymethod;
    }
}
