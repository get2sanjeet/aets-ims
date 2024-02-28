package main.java.models;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Author: Afif Al Mamun
 * Written on: 8/1/2018
 * Project: TeslaRentalInventory
 **/
@Data
@AllArgsConstructor
public class Employee {
    String username;
    String pass;
    String email;
    String access;
}
