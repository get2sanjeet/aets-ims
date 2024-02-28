package main.java.models;

import lombok.Data;

import javax.persistence.*;

/**
 * Author: Afif Al Mamun
 * Written on: 7/22/2018
 * Project: TeslaRentalInventory
 **/
@Data
@Entity
@Table(name = "producttype")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "productTypeID")
    Integer id;

    @Column(name = "productName")
    String productName;

    @Column(name = "productModel")
    String productModel;

    @Column(name = "productStockCount")
    Integer productStockCount;

    @Column(name = "productPrice")
    Integer price;

    public Stock() {}

    public Stock(Integer id, String productName, String productModel, Integer productStockCount, Integer price) {
        this.id = id;
        this.productName = productName;
        this.productModel = productModel;
        this.productStockCount = productStockCount;
        this.price = price;
    }

    public Stock(String productName, String productModel, Integer productStockCount, Integer price) {
        this.productName = productName;
        this.productModel = productModel;
        this.productStockCount = productStockCount;
        this.price = price;
    }
}
