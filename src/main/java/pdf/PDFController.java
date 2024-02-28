package main.java.pdf;

import main.java.models.SalesOrder;

import java.util.*;

public class PDFController {
    public static void main(String[] args) {
        createInvoice(null,"");
    }

    public static void createInvoice(SalesOrder salesOrder, String path) {
        if (null != salesOrder) {
            List<InvoiceData> invoiceDataList = createData(salesOrder);

            CompanyInfo buyer = new Buyer("Apex Innovations Ltd", "123 Tech, Suite 567, Silicon Valley, CA 94000",
                    "US123456789", "+1 (555) 123-4567", "apex@gmail.com");
            CompanyInfo seller = new Seller("Andaman Electro Tech Solutions", "Near TSG, Prem Nagar, Port Blair. 744101",
                    "US987654321", "+91 7842994836", "global@gmail.com");

            InvoiceGenerator invoiceGenerator = new InvoiceGenerator("", buyer, seller, invoiceDataList, salesOrder.getInvoiceNumber(), salesOrder.getOrderDate());
            invoiceGenerator.createInvoice(path);
        }
    }

    private static List<InvoiceData> createData(SalesOrder salesOrder) {
        List<InvoiceData> invoiceDataList = new ArrayList<>();
        salesOrder.getOrderItems().stream().forEach(orderItem -> {
            invoiceDataList.add(new InvoiceData(orderItem.getSerial(), "Croptech - "+orderItem.getName()+" "+ orderItem.getModel()+"W", orderItem.getQty(), orderItem.getUnitPrice()));
        });
        return invoiceDataList;
    }
}
