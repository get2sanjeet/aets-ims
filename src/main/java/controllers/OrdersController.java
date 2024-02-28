package main.java.controllers;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Lighting;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import main.java.barcode.BarCodeUtility;
import main.java.barcode.BarcodeAccumulator;
import main.java.entitymanager.TransactionManager;
import main.java.models.*;
import main.java.pdf.PDFController;
import org.controlsfx.control.textfield.TextFields;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Author: Afif Al Mamun
 * Written on: 7/15/2018
 * Project: TeslaRentalInventory
 **/

public class OrdersController extends Base implements Initializable {

    private static final String STEPPER_GREEN = "#20a822";

    private static final String STEPPER_BLUE = "#3369ff";
    public int i = 0;

    ObservableList<OrderItemTableModel> orderItems = FXCollections.observableArrayList();

    @FXML
    private TableView<SalesOrderTableModel> salesOrdersTbl;

    @FXML
    private TableColumn<?, ?> orderIdCol;

    @FXML
    private TableColumn<?, ?> statusCol;

    @FXML
    private TableColumn<?, ?> customerNameCol;

    @FXML
    private TableColumn<?, ?> productsCol;

    @FXML
    private TableColumn<?, ?> orderDateCol;

    @FXML
    private TableColumn<?, ?> qtyCol;

    @FXML
    private TableColumn<?, ?> totalPriceCol;

    @FXML
    private TableView<OrderItemTableModel> orderItemsTable;

    @FXML
    private TableColumn<?, ?> orderItemIDCol;

    @FXML
    private TableColumn<?, ?> orderItemNameCol;

    @FXML
    private TableColumn<?, ?> orderItemModelCol;

    @FXML
    private TableColumn<?, ?> orderItemQtyCol;

    @FXML
    private TableColumn<?, ?> orderItemPriceCol;
    @FXML
    private LineChart<String, Integer> lineChart;
    @FXML
    private FontAwesomeIconView btnChartIcon;
    @FXML
    private CategoryAxis dateAxis;
    @FXML
    private NumberAxis amountAxis;
    @FXML
    private JFXButton btnBarchart;

    @FXML
    private AnchorPane rightPane;

    @FXML
    private JFXTextField scannerInput;

    @FXML
    private JFXTextField searchKey;

    @FXML
    private FontAwesomeIconView gotoCustomerDetails2;

    @FXML
    private FontAwesomeIconView gotoCustomerDetails1;

    @FXML
    private Pane productDetailsPane;

    @FXML
    private Pane paymentDetailsPane;

    @FXML
    private Pane customerDetailsPane;

    @FXML
    private Label totalPrice;

    @FXML
    private Label cartCount;

    @FXML
    private Label deliveryFee;

    @FXML
    private Label gstPrice;

    @FXML
    private Label totalPriceWithtax;


    @FXML
    private JFXButton loadAgain;

    @FXML
    private Circle productDetailsStepper;

    @FXML
    private Line customerDetailsLine;

    @FXML
    private Line paymentDetailsLine;

    @FXML
    private Circle customerDetailsStepper;

    @FXML
    private Circle paymentDetailsStepper;

    private SalesOrder currentOrder = new SalesOrder();

    @FXML
    private JFXComboBox<String> selectCustomer;

    @FXML
    private JFXTextField customerMobile;

    @FXML
    private JFXButton gotoCustomerDetails;

    @FXML
    private JFXButton gotoPaymentDetails;

    @FXML
    private JFXTextField customerEmail;

    @FXML
    private JFXTextArea customerAddress;

    @FXML
    private TableView<PaymentTableModel> paymentOrderItemTable;

    @FXML
    private TableColumn<?, ?> paymentIdCol;

    @FXML
    private TableColumn<?, ?> paymentProductCol;

    @FXML
    private TableColumn<?, ?> paymentQtyCol;

    @FXML
    private TableColumn<?, ?> paymentPriceCol;

    @FXML
    private TableColumn<PaymentTableModel, MaterialDesignIconView> billCol;

    private List<Product> listOfScannedProducts = new ArrayList<>();


    @FXML
    void deleteOrderDetails(ActionEvent event) {

    }

    @FXML
    void focusScan(ActionEvent event) {
        scannerInput.requestFocus();
    }

    @FXML
    void gotoCustomerDetails(ActionEvent event) {
        productDetailsStepper.setFill(Paint.valueOf(STEPPER_GREEN));
        productDetailsStepper.setStroke(Paint.valueOf(STEPPER_GREEN));
        productDetailsStepper.setEffect(null);

        customerDetailsStepper.setFill(Paint.valueOf(STEPPER_GREEN));
        customerDetailsStepper.setStroke(Paint.valueOf(STEPPER_GREEN));
        customerDetailsStepper.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.web("#44f86e"), 5, 0.05, 0, 1));

        customerDetailsLine.setFill(Paint.valueOf(STEPPER_GREEN));
        customerDetailsLine.setStroke(Paint.valueOf(STEPPER_GREEN));

        paymentDetailsStepper.setEffect(null);

        productDetailsPane.setVisible(false);
        paymentDetailsPane.setVisible(false);
        customerDetailsPane.setVisible(true);

        List<Customer> customers = TransactionManager.getCustomers();
        ObservableList<String> obsNames = FXCollections.observableArrayList();
        customers.stream().forEach(customer -> obsNames.add(customer.getName()));
        selectCustomer.setItems(obsNames);
    }

    @FXML
    void loadCustomer(ActionEvent event) {
        final String selectedItem = selectCustomer.getSelectionModel().getSelectedItem();
        Customer customer = TransactionManager.getCustomerByName(selectedItem);
        customerMobile.setText(customer.getPhone());
        customerAddress.setText(customer.getEmail());
        customerAddress.setText(customer.getAddressOne()+",\n"+customer.getAddressTwo()+",\n"+customer.getAddressThree());
        if (null != gotoPaymentDetails) {
            gotoPaymentDetails.setDisable(false);
        }
    }

    @FXML
    void gotoPaymentDetails(ActionEvent event) {
        productDetailsStepper.setFill(Paint.valueOf(STEPPER_GREEN));
        productDetailsStepper.setStroke(Paint.valueOf(STEPPER_GREEN));
        productDetailsStepper.setEffect(null);

        paymentDetailsStepper.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.web("#44f86e"), 5, 0.05, 0, 1));
        paymentDetailsStepper.setFill(Paint.valueOf(STEPPER_GREEN));
        paymentDetailsStepper.setStroke(Paint.valueOf(STEPPER_GREEN));

        customerDetailsStepper.setFill(Paint.valueOf(STEPPER_GREEN));
        customerDetailsStepper.setStroke(Paint.valueOf(STEPPER_GREEN));
        customerDetailsStepper.setEffect(null);

        customerDetailsLine.setFill(Paint.valueOf(STEPPER_GREEN));
        paymentDetailsLine.setFill(Paint.valueOf(STEPPER_GREEN));

        productDetailsPane.setVisible(false);
        paymentDetailsPane.setVisible(true);
        customerDetailsPane.setVisible(false);

        ObservableList<PaymentTableModel> paymentTableModels = FXCollections.observableArrayList();
        paymentIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        paymentProductCol.setCellValueFactory(new PropertyValueFactory<>("product"));
        paymentQtyCol.setCellValueFactory(new PropertyValueFactory<>("qty"));
        paymentPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        orderItems.forEach(orderItem -> {
            paymentTableModels.add(new PaymentTableModel(Integer.valueOf(orderItem.getId()), orderItem.getProductName()+"("+orderItem.getProductModel()+")", orderItem.getQty(), orderItem.getPerPeicePrice()));
        });
        paymentOrderItemTable.setItems(paymentTableModels);

        AtomicReference<Double> sumAllItemPrice = new AtomicReference<>((double) 0);
        double deliveryPrice = 20.0;
        double totalAllPricewithTax;
        orderItems.stream().forEach(orderItem -> sumAllItemPrice.set(sumAllItemPrice.get() + Double.valueOf(orderItem.getPerPeicePrice() * orderItem.getQty())));
        double gst = Math.round((sumAllItemPrice.get() * 0.18)*100)/100;
        totalAllPricewithTax = sumAllItemPrice.get() + deliveryPrice + gst;

        deliveryFee.setText(String.valueOf(deliveryPrice));
        totalPriceWithtax.setText(String.valueOf(totalAllPricewithTax));
        totalPrice.setText(String.valueOf(sumAllItemPrice));
        gstPrice.setText(String.valueOf(gst));
    }

    @FXML
    void gotoProductDetails(ActionEvent event) {
        productDetailsPane.setVisible(true);
        paymentDetailsPane.setVisible(false);
        customerDetailsPane.setVisible(false);
    }

    @FXML
    void onKeyTypedEvent(KeyEvent event) {
        try {
            new BarcodeAccumulator().accumulate(scannerInput, event, this);
        } catch (Exception e) {
            scannerInput.clear();
            new PromptDialogController("Error!", e.getMessage());
        }
    }

    @FXML
    void saveOrder(ActionEvent event) {
        Platform.runLater(()->{
            AtomicReference<Double> sumAllItemPrice = new AtomicReference<>((double) 0);
            double deliveryPrice = 20.0;
            double totalAllPricewithTax;
            orderItems.stream().forEach(orderItem -> sumAllItemPrice.set(sumAllItemPrice.get() + Double.valueOf(orderItem.getPerPeicePrice() * orderItem.getQty())));
            double gst = sumAllItemPrice.get() * 0.18;
            totalAllPricewithTax = sumAllItemPrice.get() + deliveryPrice + gst;

            SalesOrder order = new SalesOrder();
            order.setTax(gst);
            order.setPrice(sumAllItemPrice.get());
            order.setTotalPrice(totalAllPricewithTax);
            order.setProducts(listOfScannedProducts);
            order.setOrderDate(new Timestamp(System.currentTimeMillis()));
            order.setCustomer(TransactionManager.getCustomerByName(selectCustomer.getSelectionModel().getSelectedItem()));
            try {
                TransactionManager.save(order);
                new PromptDialogController("Success! ", "Order was placed successfully");
                //Platform.runLater(()-> markProductsSold(listOfScannedProducts.stream().map(Product::getId).collect(Collectors.toList())));
                resetOrderForm();
            } catch (Exception e){
                new PromptDialogController("Error! "+e.getMessage(), e.getLocalizedMessage());
            }
        });

    }

    private void markProductsSold(List<Integer> productIds) {
        TransactionManager.updateProductAsSold(productIds);
    }

    private void resetOrderForm() {
        listOfScannedProducts.clear();
        orderItems.clear();
        selectCustomer.getSelectionModel().selectFirst();
        productDetailsPane.setVisible(true);
        customerDetailsPane.setVisible(false);
        paymentDetailsPane.setVisible(false);
    }

    /**
     * This field will be used to transition b/w Table and Chart
     * toggleTable = true; Table View is being showed
     * toggleTable = false; LineChart is being showed
     */
    private static boolean toggleTable = true;

    private void generateLineChart() {
        lineChart.getData().clear();
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT purchaseDate, sum(payAmount) FROM purchases WHERE User_username='" + LogInController.loggerUsername + "' GROUP BY purchaseDate ORDER BY UNIX_TIMESTAMP(purchaseDate) DESC LIMIT 15");
            ResultSet rs = ps.executeQuery();

            XYChart.Series chartData = new XYChart.Series<>();

            while (rs.next()) {
                chartData.getData().add(new XYChart.Data(rs.getString(1), rs.getInt(2)));
            }

            lineChart.getData().addAll(chartData);

        } catch (SQLException e) {
            e.printStackTrace();
            new PromptDialogController("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + e.getErrorCode());
        }
    }

    @FXML
    void btnBarchartAction(ActionEvent event) {
        if (toggleTable) { //If toggleTable is true that means table view is currently in view
            toggleTable = false; //Changing toggleTable value
            btnChartIcon.setGlyphName("TABLE");
            salesOrdersTbl.setVisible(false);
            lineChart.setVisible(true);

            generateLineChart();

        } else {
            toggleTable = true;
            btnChartIcon.setGlyphName("LINE_CHART");
            lineChart.setVisible(false);
            lineChart.getData().clear();
            salesOrdersTbl.setVisible(true);
        }
    }


    /**
     * startTransaction variable will be used to verify inputs - That is
     * if all the inputs are OK then start transaction, otherwise not
     */
    private static boolean startTransaction = false;

    public static ObservableList<SalesOrderTableModel> salesOrderTableModelList;
    public static ArrayList<String> customerIDName = null; //Will hold auto completion data for customer ID text field
    //The field will be initiated in InitializerController class
    public static ArrayList<String> inventoryItem = null;
    public static ArrayList<Integer> customerID = null;
    public static ArrayList<Integer> itemIDForSale = null;

    @FXML
    void loadAgain(ActionEvent event) {

        /*List<SalesOrder> salesOrders = TransactionManager.getAllSalesOrders();
        salesOrderTableModelList = FXCollections.observableArrayList();
        if (salesOrders.size() > 0) {
            salesOrders.forEach(so -> {
                JFXButton bill = createBillButton(so.getId());
                HBox status = new HBox();
                setHboxData(status, true, false, false);
                salesOrderTableModelList.add(new SalesOrderTableModel(so.getId().toString(), status,
                        so.getCustomer().getName(),
                        so.getProducts().stream().map(Product::getProductName).toString(),
                        String.valueOf(new Date()),
                        so.getProducts().size(),
                        Double.valueOf(99), bill));
            });
        }
            // Resetting Fields
        salesOrdersTbl.getItems().clear();
        salesOrdersTbl.setItems(salesOrderTableModelList);*/

        loadSalesOrdersTable(null);

        // Resetting lineChart if toggleTable is false
        if (!toggleTable) {
            generateLineChart();
        }
    }

    private void printBill(int id) {
        SalesOrder salesOrder = TransactionManager.getSalesOrderById(id);
        System.out.println("Downloading - "+ salesOrder.getId());
        String billPath = BarCodeUtility.BILL_PATH + "INVOICE-" + salesOrder.getId() +".pdf";
        PDFController pdfController = new PDFController();
        pdfController.createInvoice(salesOrder, billPath);
        try {
            Desktop.getDesktop().print(new File(billPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> scannerInput.requestFocus());
        toggleTable = true;
        startTransaction = false;
        loadSalesOrdersTable(null);
        TextFields.bindAutoCompletion(searchKey, customerIDName);

    }

    private void loadSalesOrdersTable(List<SalesOrder> orders) {
        Platform.runLater(() -> {
            List<SalesOrder> salesOrders = orders != null ? orders : TransactionManager.getAllSalesOrders();
            salesOrders = salesOrders.stream().sorted(Comparator.comparing(SalesOrder::getOrderDate).reversed()).collect(Collectors.toList());
            salesOrderTableModelList = FXCollections.observableArrayList();
            if (salesOrders.size() > 0) {
                salesOrders.forEach(so -> {
                    try {
                        JFXButton bill = createBillButton(so.getId());
                        HBox status = new HBox();
                        setHboxData(status, true, false, false);
                        salesOrderTableModelList.add(new SalesOrderTableModel(so.getInvoiceNumber(), status,
                                so.getCustomer().getName(),
                                so.getProducts().stream().map(Product::getProductName).distinct().collect(Collectors.joining(",")),
                                new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(so.getOrderDate() == null? String.valueOf(new Timestamp(System.currentTimeMillis())) :so.getOrderDate().toString())),
                                so.getProducts().size(),
                                Double.valueOf(so.getTotalPrice()), bill));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            // Setting up table on load
            orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderID"));
            statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
            customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
            productsCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
            orderDateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
            qtyCol.setCellValueFactory(new PropertyValueFactory<>("qty"));
            totalPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
            billCol.setCellValueFactory(new PropertyValueFactory<>("billDownload"));

            salesOrdersTbl.setItems(salesOrderTableModelList);

            salesOrdersTbl.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2)
                {
                    orderItems.clear();
                    Integer id = Integer.valueOf(salesOrdersTbl.getSelectionModel().getSelectedItem().getOrderID());
                    if (null != id){
                        SalesOrder salesOrder = TransactionManager.getSalesOrderById(id);
                        loadOrderFormData(salesOrder.getOrderItems());
                        loadCustomerInfo(salesOrder.getCustomer());
                        loadPaymentInfo(salesOrder.getCustomer());
                    }
                }
            });
        });
    }

    private void loadPaymentInfo(Customer customer) {
    }

    private void loadCustomerInfo(Customer customer) {
        selectCustomer.getSelectionModel().select(customer.getName());
        customerAddress.setText(customer.getAddress());
        customerMobile.setText(customer.getPhone());
        customerEmail.setText(customer.getEmail());
    }

    private void loadOrderFormData(List<OrderItem> list) {
        setOrderItemForm();
        AtomicInteger count = new AtomicInteger();
        if (list != null) {
            list.forEach(item->{
                OrderItemTableModel orderItem = new OrderItemTableModel(String.valueOf(count.incrementAndGet()), item.getName(), item.getModel(), Integer.valueOf((int) item.getUnitPrice()), item.getQty());
                orderItems.add(orderItem);
            });
            cartCount.setText(String.valueOf(list.size()));
        }
        orderItemsTable.setItems(orderItems);
        gotoCustomerDetails.setDisable(false);
    }

    private void setHboxData(HBox status,boolean isCreated,boolean isDelivered,boolean isPaid){
        Rectangle rectangle = new Rectangle();
        rectangle.setHeight(3.0);
        rectangle.setWidth(25.0);
        rectangle.setFill(isCreated?Paint.valueOf("GREEN"):Paint.valueOf("RED"));
        rectangle.setStroke(isCreated?Paint.valueOf("GREEN"):Paint.valueOf("RED"));
        status.getChildren().add(rectangle);

        Rectangle rectangle1 = new Rectangle();
        rectangle1.setHeight(3.0);
        rectangle1.setWidth(25.0);
        rectangle1.setFill(isDelivered?Paint.valueOf("GREEN"):Paint.valueOf("RED"));
        rectangle1.setStroke(isDelivered?Paint.valueOf("GREEN"):Paint.valueOf("RED"));
        status.getChildren().add(rectangle1);

        Rectangle rectangle2 = new Rectangle();
        rectangle2.setHeight(3.0);
        rectangle2.setWidth(25.0);
        rectangle2.setFill(isPaid?Paint.valueOf("GREEN"):Paint.valueOf("RED"));
        rectangle2.setStroke(isPaid?Paint.valueOf("GREEN"):Paint.valueOf("RED"));
        status.getChildren().add(rectangle2);
    }

    private JFXButton createBillButton(Integer id) {

        FontAwesomeIconView icon = new FontAwesomeIconView();
        icon.setGlyphName("PRINT");
        icon.setSize("18.0");
        icon.setId(String.valueOf(id));
        icon.setFill(Paint.valueOf("#2bbb15"));
        icon.setEffect(new Lighting());
        JFXButton bill = new JFXButton("", icon);
        bill.setPrefHeight(25.0);
        bill.setPrefWidth(25.0);
        bill.setEffect(new DropShadow());
        bill.setOnAction(newEvent -> printBill(Integer.valueOf(id)));
        return bill;
    }

    @FXML
    void btnProceedAction(ActionEvent event) {

    }

    @Override
    public void fetchAndPopulateBarcodeData() throws Exception {

        orderItemsTable.getItems().clear();
        String barcode = scannerInput.getText().trim();
        if (barcode.length() == 12) {
            barcode = "0"+barcode;
        }
        System.out.println("barcode :" +barcode);
        Product product = TransactionManager.getProductByBarcodeSerialNumber(barcode);
        if (product.isSold()) {
            throw new Exception("Barcode Invalid! Already sold.");
        }
        setOrderItemForm();

        if (orderItems.size() > 0) {
            Optional<OrderItemTableModel> optionalOrderItem =
                    orderItems.stream()
                    .filter(orderItem -> orderItem.getProductName().equals(product.getProductName())
                                            && orderItem.getProductModel().equals(product.getProductModel()))
                    .findFirst();
            if (optionalOrderItem.isPresent()) {
                OrderItemTableModel oi = optionalOrderItem.get();
                int qty = oi.getQty() + 1;
                OrderItemTableModel newOrderItem = new OrderItemTableModel(oi.getId(), oi.getProductName(), oi.getProductModel(), product.getPerPeicePrice() * qty, qty);

               orderItems.remove(oi);
                orderItems.add(newOrderItem);

            } else {
                OrderItemTableModel oiId = orderItems.stream().collect(Collectors.maxBy(Comparator.comparing(OrderItemTableModel::getId))).get();
                OrderItemTableModel orderItem = new OrderItemTableModel(oiId.getId(), product.getProductName(), product.getProductModel(), Integer.valueOf(product.getPerPeicePrice()), 1);
                orderItems.add(orderItem);
            }
        } else {
            OrderItemTableModel orderItem = new OrderItemTableModel(String.valueOf(1001), product.getProductName(), product.getProductModel(), Integer.valueOf(product.getPerPeicePrice()), 1);
            orderItems.add(orderItem);
        }
        if (orderItems.size() > 0) {
            gotoCustomerDetails.setDisable(false);
            listOfScannedProducts.add(product);
            cartCount.setText(String.valueOf(orderItems.size()));
        }
        orderItemsTable.getItems().addAll(orderItems);
    }

    @FXML
    void performSearch(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER)) {
            String searchKeyVal = searchKey.getText();
            System.out.println(searchKeyVal+" searching...");
            boolean isId = isDigits(searchKeyVal);
            if (isId) {
                SalesOrder order = TransactionManager.getSalesOrderById(Integer.valueOf(searchKeyVal));
                loadSalesOrdersTable(Arrays.asList(order));
            }
            else
                TransactionManager.getSalesOrdersByCustomerId(0);
        }
    }

    public static boolean isDigits(String str)
    {

        // Traverse the string from
        // start to end
        for (int i = 0; i < str.length(); i++) {

            // Check if the specified
            // character is a not digit
            // then return false,
            // else return false
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        // If we reach here that means all
        // the characters were digits,
        // so we return true
        return true;
    }

    private void setOrderItemForm() {
        orderItemIDCol.setCellValueFactory(new PropertyValueFactory<>("invoiceNumber"));
        orderItemNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        orderItemModelCol.setCellValueFactory(new PropertyValueFactory<>("productModel"));
        orderItemQtyCol.setCellValueFactory(new PropertyValueFactory<>("qty"));
        orderItemPriceCol.setCellValueFactory(new PropertyValueFactory<>("perPeicePrice"));
    }
}
