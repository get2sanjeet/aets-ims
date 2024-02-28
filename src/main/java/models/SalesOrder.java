package main.java.models;

import lombok.Data;
import main.java.pdf.InvoiceData;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Data
@Entity
@Table(name = "sales_order")
public class SalesOrder {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_order_fk")
    private List<Product> products;

    @Column(name = "price")
    private Double price;

    @Column(name = "tax")
    private Double tax;

    @Column(name = "total_price")
    private Double totalPrice;

    @Column(name = "order_date")
    private Timestamp orderDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_fk")
    private Customer customer;

    @Transient
    private String invoiceNumber;

    @Transient
    public List<OrderItem> orderItems;

    public String getInvoiceNumber() {
        this.invoiceNumber = "000"+this.id;
        return this.invoiceNumber;
    }

    public List<OrderItem> getOrderItems() {
        int serial = 0;
        List<OrderItem> orderItems = new ArrayList<>();
        Map<String, List<Product>> productMap = this.getProducts().stream()
                .collect(Collectors.groupingBy(p -> p.getProductName() +"-"+ p.getProductModel(), Collectors.mapping((Product p) -> p, toList())));
        for (Map.Entry<String,List<Product>> entry : productMap.entrySet()) {
            String name = entry.getKey().split("-")[0];
            String model = entry.getKey().split("-")[1];
            int quantity = entry.getValue().size();
            int price = 0;
            if (entry.getValue().size() > 0) {
                price = entry.getValue().get(0).getPerPeicePrice();
            }
            orderItems.add(new OrderItem( ++serial,name, model, quantity, price));
        }
        this.orderItems = orderItems;
        return this.orderItems;
    }

    @Override
    public String toString() {
        return "SalesOrder{" +
                "id=" + id +
                ", products=" + products +
                ", price=" + price +
                ", tax=" + tax +
                ", totalPrice=" + totalPrice +
                ", customer=" + customer +
                ", orderDate=" + orderDate +
                '}';
    }
}
