package main.java.controllers;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.java.async.AsyncBarcodeGeneratorController;
import main.java.barcode.BarCodeUtility;
import main.java.async.AsyncTaskManager;
import main.java.barcode.BarcodeProcessor;
import main.java.entitymanager.TransactionManager;
import main.java.models.*;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.krysalis.barcode4j.BarcodeException;

import java.io.IOException;
import java.net.URL;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Author: Afif Al Mamun
 * Written on: 09-Jul-18
 * Project: TeslaRentalInventory
 **/
public class InventoryController implements Initializable{
    @FXML
    public HBox productHbox;
    @FXML
    public Label led9WCount;

    @FXML
    public Label led15WCount;

    @FXML
    public Label led22WCount;


    @FXML
    private AnchorPane addTypePane;

    @FXML
    private AnchorPane itemPane;

    @FXML
    private TextField newStockCount;

    @FXML
    private JFXTextField perPeicePrice;

    @FXML
    private JFXComboBox<String> productModels;

    @FXML
    private TableColumn<Stock, String> productName;

    @FXML
    private JFXComboBox<String> productNames;

    @FXML
    private TableView<ProductStockTableModel> productStocksTable;

    @FXML
    private Label productTypeId;

    @FXML
    private TableColumn<?, ?> productAvailableStockCol;

    @FXML
    private TableColumn<?, ?> productModelCol;

    @FXML
    private TableColumn<?, ?> productNameCol;

    @FXML
    private TableColumn<?, ?> productPriceCol;

    @FXML
    private TableColumn<?, ?> productTypeIdCol;

    @FXML
    public Label notification;

    @FXML
    private JFXButton stockUploadBtn;

    @FXML
    private AnchorPane mainAnchor;

    @FXML
    private JFXTextField productQty;
    @FXML
    private ProgressIndicator barcodeProgressIndicator;

    @FXML
    private Label txtBarcodeGenerated;

    @FXML
    private Label txtBarcodeGenerating;

    @FXML
    private AnchorPane loaderPane;

    @FXML
    private AnchorPane formPane;

    @FXML
    private FontAwesomeIconView iconBarcodeGenerated;

    @FXML
    private Label countOfBarcode;

    public static List<ProductSpecification> productSpecifications;


    @FXML
    void addProductType(ActionEvent event) {
        Stock productType = new Stock(productNames.getSelectionModel().getSelectedItem(),
                productModels.getSelectionModel().getSelectedItem(),
                Integer.parseInt(newStockCount.getText()),
                Integer.parseInt(perPeicePrice.getText()));
        TransactionManager.save(productType);
        clearProductTypeForm();
        loadProductStockTable();
        notification.setText("Data saved successfully!!");
        notification.setTextFill(Color.web("#2ECC71"));
    }

    @FXML
    void loadStockScanWindow(ActionEvent event) {
        loadWindow("Stock Scanner", "/main/resources/view/stockuploadmanager.fxml");
    }

    @FXML
    void loadGenerateBarcodeWindow(ActionEvent event) {
        loadWindow("Barcode Generator", "/main/resources/view/barcodegenerator.fxml");
    }

    private void clearProductTypeForm() {
        productNames.getSelectionModel().selectFirst();
        productModels.getSelectionModel().selectFirst();
        newStockCount.setText(null);
        perPeicePrice.setText(null);
    }

    void loadWindow(String title, String URL) {
        try {
            Parent acc = FXMLLoader.load(getClass().getResource(URL));
            Scene s = new Scene(acc);
            Stage stg = new Stage();
            stg.initModality(Modality.APPLICATION_MODAL);
            stg.setTitle(title);
            stg.setScene(s);
            stg.setResizable(false);
            stg.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TreeMap<String, Integer> itemType = new TreeMap<>();
    public static ObservableList<Item> itemList = FXCollections.observableArrayList(); //This field will auto set from InitializerController Class
    public static ObservableList<Item> tempList = FXCollections.observableArrayList(); //Will hold the main list while searching
    public static ArrayList<String> itemNames = new ArrayList<>();
    public static ObservableList<String> itemTypeNames = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadTableAsync();
        loadProductSpecification();
    }

    private void loadTableAsync() {
        AsyncTaskManager task = new AsyncTaskManager(this);
        task.execute();
    }

    private void reloadRecords(){
        ObservableList<String> items = FXCollections.observableArrayList("9 watt", "12 watt", "15 watt", "22 watt");
        productModels.setItems(items);

        ObservableList<String> productNameList = FXCollections.observableArrayList("LED", "CFL", "GLS", "Incandescent");
        productModels.setItems(items);
        productNames.setItems(productNameList);

        itemList.clear();
        Connection connection = DBConnection.getConnection();
        try {
            PreparedStatement getItemList = connection.prepareStatement("SELECT *" +
                    "FROM item, itemtype WHERE item.ItemType_itemTypeId = itemtype.itemTypeId ORDER BY itemID");
            ResultSet itemResultSet = getItemList.executeQuery();

            while(itemResultSet.next()) {
                Item item = new Item(itemResultSet.getInt("itemID"),
                        itemResultSet.getString("itemName"),
                        itemResultSet.getInt("stock"),
                        false,
                        false,
                        itemResultSet.getDouble("salePrice"),
                        itemResultSet.getDouble("rentRate"),
                        itemResultSet.getString("photo"),
                        itemResultSet.getString("typeName"));

                itemNames.add(itemResultSet.getString("itemName"));

                if(itemResultSet.getString("rentalOrSale").contains("Rental"))
                {
                    item.setRent(true);
                }
                if(itemResultSet.getString("rentalOrSale").contains("Sale")) {
                    item.setSale(true);
                }

                itemList.add(item);

            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Object loadProductStockTable(){
        productStocksTable.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2)
            {
                //loadContents();
            }
        });

        ObservableList<ProductStockTableModel> list = FXCollections.observableArrayList();
        List<ProductStockTableModel> tempSortedList = new ArrayList<>();

        productTypeIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productModelCol.setCellValueFactory(new PropertyValueFactory<>("productModel"));
        productAvailableStockCol.setCellValueFactory(new PropertyValueFactory<>("stockCount"));
        productPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        try {
            List<ProductSpecification> productSpecification = TransactionManager.getProductSpecifications();
            Map<String, List<Product>> stockMap = new HashMap<>();
            productSpecification.stream().forEach(productType ->{
                List<Product> productList = TransactionManager.getProductByNameModel(productType.getName(), productType.getModel());
                stockMap.put(productType.getName() + "-" + productType.getModel(), productList);

            });
            int id = 1;
            for (Map.Entry<String,List<Product>> entry : stockMap.entrySet()){
                String name = entry.getKey().split("-")[0];
                String model = entry.getKey().split("-")[1];
                int stockCount = entry.getValue().size();
                int price = 0;
                if (entry.getValue().size()>0){
                    price = entry.getValue().get(0).getPerPeicePrice();
                }
                tempSortedList.add(new ProductStockTableModel(id, name, model, stockCount,price));
                id++;
            }
            tempSortedList = tempSortedList.stream().sorted(Comparator.comparing(ProductStockTableModel::getProductName)).collect(Collectors.toList());
            list.addAll(tempSortedList);
            productStocksTable.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void loadContents() {
        /*ProductStockTableModel selectedProductType = productStocksTable.getSelectionModel().getSelectedItem();
        productTypeId.setText(String.valueOf(selectedProductType.getId()));
        productNames.getSelectionModel().select(selectedProductType.getProductName());
        productModels.getSelectionModel().select(selectedProductType.getProductModel());
        perPeicePrice.setText(String.valueOf(selectedProductType.getPrice()));
        newStockCount.setText(String.valueOf(selectedProductType.getStockCount()));
        try {
            imgBarcode.setImage(new Image(BarCodeUtility.getBarcode(BarCodeUtility.prepareProductCode())));
        } catch (IOException | ConfigurationException | BarcodeException e) {
            throw new RuntimeException(e);
        }*/
    }

    @FXML
    void hideBarcodePane(ActionEvent event) {
        loaderPane.setVisible(false);
        formPane.setVisible(true);
    }

    @FXML
    void generateBarcodes(ActionEvent event) {
        if (isInputValid()) {
            formPane.setVisible(false);
            loaderPane.setVisible(true);
            AsyncBarcodeGeneratorController barcodeGeneratorController = new AsyncBarcodeGeneratorController(this);
            barcodeGeneratorController.execute();
        }
    }

    private boolean isInputValid() {
        if (perPeicePrice.getText() == null
                || productQty.getText() == null
                || productNames.getSelectionModel().getSelectedItem() == null
                || productModels.getSelectionModel().getSelectedItem() == null
                || perPeicePrice.getText().trim().equals("")
                || productQty.getText().trim().equals("")
                || productNames.getSelectionModel().getSelectedItem().trim().equals("")
                || productModels.getSelectionModel().getSelectedItem().trim().equals("")
                || Integer.valueOf(perPeicePrice.getText()) == 0
                || Integer.valueOf(productQty.getText()) == 0) {
            perPeicePrice.setText("");
            productQty.setText("");
            perPeicePrice.setUnFocusColor(Color.RED);
            productQty.setUnFocusColor(Color.RED);
            productModels.setUnFocusColor(Color.RED);
            productNames.setUnFocusColor(Color.RED);
            return false;
        }
        return true;
    }

    public Object createBarcode() {
        barcodeProgressIndicator.setVisible(true);
        int qty = Integer.valueOf(productQty.getText());
        double oneUnitProgress = Double.valueOf(1)/qty;
        for (int i = 0; i < qty; i++) {
            createProductAndBarcode();
            double p = oneUnitProgress * (i + 1);
            barcodeProgressIndicator.setProgress(p);
            int finalI = i;
            Platform.runLater(()-> countOfBarcode.setText(String.valueOf(finalI +1)));
        }
        return null;
    }

    private void createProductAndBarcode() {
        Product product = new Product();
        product.setProductModel(getSanitizedText(productModels.getSelectionModel().getSelectedItem()));
        product.setProductName(productNames.getSelectionModel().getSelectedItem());
        product.setCreated(true);
        product.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        product.setPerPeicePrice(Integer.valueOf(perPeicePrice.getText()));
        product.setSold(false);
        int productTypeId = TransactionManager.getProductTypesByNameModel(productNames.getSelectionModel().getSelectedItem(), getSanitizedText(productModels.getSelectionModel().getSelectedItem()));
        System.out.println("productTypeId-" +productTypeId);
        Integer maximumProductId = TransactionManager.getMaximumProductId();
        maximumProductId = null == maximumProductId ? 0 : maximumProductId;
        try {
            String barcodeSerialNumber = BarCodeUtility.getBarcodeSerialNumber(BarCodeUtility.prepareProductCode(productTypeId, maximumProductId));
            System.out.println("barcodeSerialNumber -"+barcodeSerialNumber);
            /*BarcodeProcessor.process(barcodeSerialNumber, product.getProductName(), product.getProductModel(), product.getPerPeicePrice());
            System.out.println("barcodeSerialNumber -"+barcodeSerialNumber+"-Processed");*/
            product.setBarcodeSerialNumber(barcodeSerialNumber);
            TransactionManager.save(product);
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        } catch (BarcodeException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(product);
    }

    private String getSanitizedText(String model) {
        if (model == null || model == "") {
            return null;
        }
        model = model.replaceAll("[A-Za-z_@/#&+-,]", "");
        model = model.replaceAll(" ", ",");
        return model;
    }

    @FXML
    void loadPrice(ActionEvent event) {
        String name = productNames.getSelectionModel().getSelectedItem();
        String model = getSanitizedText(productModels.getSelectionModel().getSelectedItem());
        Integer price = productSpecifications
                .stream()
                .filter(ps -> ps.getName().equalsIgnoreCase(name) && ps.getModel().equalsIgnoreCase(model))
                .map(productSpecification -> productSpecification.getPrice())
                .findFirst()
                .get();
        perPeicePrice.setText(String.valueOf(price));
    }

    @FXML
    void loadWatts(ActionEvent event) {
        String name = productNames.getSelectionModel().getSelectedItem();
        List<String> models = productSpecifications
                .stream()
                .filter(ps -> ps.getName().equals(name))
                .map(ps -> ps.getModel()+"W")
                .collect(Collectors.toList());
        ObservableList<String> pms = FXCollections.observableArrayList();
        pms.addAll(models);
        productModels.setItems(pms);
    }
    private void loadProductSpecification() {
        ObservableList<String> pns = FXCollections.observableArrayList();
        productSpecifications.forEach(ps -> pns.add(ps.getName()));
        productNames.setItems(pns);
    }
}
