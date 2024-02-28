package main.java.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: Afif Al Mamun
 * Written on: 7/24/2018
 * Project: TeslaRentalInventory
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    int id;
    String name;
    int stock;
    boolean rent, sale;
    double salePrice;
    double rentRate;
    String photo;
    String itemType;
}
