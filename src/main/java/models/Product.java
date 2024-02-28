package main.java.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Data
@AllArgsConstructor
@Entity
@Table(name = "product")
public class Product {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "name")
    private String productName;

    @Column(name = "model")
    private String productModel;

    @Column(name = "price")
    private int perPeicePrice;

    @Column(name = "created")
    private boolean created;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "sold")
    private boolean isSold;

    @Column(name = "barcode_serial_number", length = 100, unique = true, nullable = false)
    private String barcodeSerialNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_order_fk")
    private SalesOrder order;

    public Product(int id, String productName, String productModel, int perPeicePrice, String barcodeSerialNumber) {
        this.id = id;
        this.productName = productName;
        this.productModel = productModel;
        this.perPeicePrice = perPeicePrice;
        this.barcodeSerialNumber = barcodeSerialNumber;
    }

    public Product() {

    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", productModel='" + productModel + '\'' +
                ", perPeicePrice=" + perPeicePrice +
                ", created=" + created +
                ", createdDate=" + createdDate +
                ", isSold=" + isSold +
                ", barcodeSerialNumber='" + barcodeSerialNumber + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return getProductName().equals(product.getProductName()) && getProductModel().equals(product.getProductModel());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProductName(), getProductModel());
    }

    public boolean isSold() {
        return order != null;
    }
}
