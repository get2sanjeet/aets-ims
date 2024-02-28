package main.java.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

/**
 * Author: Afif Al Mamun
 * Written on: 7/22/2018
 * Project: TeslaRentalInventory
 **/
@Data
@AllArgsConstructor
@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;
    @Column(name = "name")
    String name;

    @Column(name = "address_one")
    String addressOne;

    @Column(name = "address_two")
    String addressTwo;

    @Column(name = "address_three")
    String addressThree;

    @Transient
    String address;

    @Column(name = "phone")
    String phone;
    @Column(name = "email")
    String email;
    @Column(name = "photo")
    String photo;
    @Column(name = "gender")
    String gender;
    @Column(name = "create_date")
    Date createDate;

    public Customer(Integer id, String name, String lastName, String addressOne, String phone, String email, String photo, String gender, Date createDate) {
        this.id = id;
        this.name = name;
        this.addressOne = addressOne;
        this.addressTwo = addressTwo;
        this.addressThree= addressThree;
        this.phone = phone;
        this.email = email;
        this.photo = photo;
        this.gender = gender;
        this.createDate = createDate;
    }

    public Customer(Integer id, String name, String addressOne, String addressTwo, String addressThree, String phone, String email, String photo, String gender, Date createDate) {
        this.id = id;
        this.name = name;
        this.addressOne = addressOne;
        this.addressTwo = addressTwo;
        this.addressThree= addressThree;
        this.phone = phone;
        this.email = email;
        this.photo = photo;
        this.gender = gender;
        this.createDate = createDate;
    }

    public Customer() {

    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + name + '\'' +
                ", address='" + addressOne + '\'' +
                ", address='" + addressTwo + '\'' +
                ", address='" + addressThree + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", photo='" + photo + '\'' +
                ", gender='" + gender + '\'' +
                ", createDate=" + createDate +
                '}';
    }
    public String getAddress(){
        return addressOne+", "+addressTwo+", "+addressThree;
    }
}
