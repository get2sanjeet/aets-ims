package main.java.controllers;

import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import main.java.barcode.BarcodeAccumulator;
import main.java.entitymanager.TransactionManager;
import main.java.models.Product;

import java.net.URL;
import java.util.ResourceBundle;

public class StockScanController extends Base implements Initializable {

    @FXML
    private FontAwesomeIconView btnAddIcon1;

    @FXML
    private FontAwesomeIconView btnAddIcon2;

    @FXML
    private AnchorPane mainAnchor;

    @FXML
    private TableView<Product> stockUploadTable;

    @FXML
    private TableColumn<?, ?> id;

    @FXML
    private TableColumn<?, ?> barcodeSerial;

    @FXML
    private TableColumn<?, ?> productName;

    @FXML
    private TableColumn<?, ?> productModel;

    @FXML
    private TableColumn<?, ?> productPrice;

    @FXML
    private JFXTextField scannerInput;
    @FXML
    void uploadStock(ActionEvent event) {

    }

    @FXML
    void validateStock(ActionEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> scannerInput.requestFocus());

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

    public void fetchAndPopulateBarcodeData() {
        String barcode = scannerInput.getText().trim();/* {"0118925939500","0200738236645","0135727741292","0132167529541","0242512288853","0115899829875","0200738236645","0135727741292","0132167529541","0242512288853","0115899829875","0118925939500"}*/;
        if (barcode.length() == 12) {
            barcode = "0"+barcode;
        }
        Product product = TransactionManager.getProductByBarcodeSerialNumber(barcode);

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        barcodeSerial.setCellValueFactory(new PropertyValueFactory<>("barcodeSerialNumber"));
        productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productModel.setCellValueFactory(new PropertyValueFactory<>("productModel"));
        productPrice.setCellValueFactory(new PropertyValueFactory<>("perPeicePrice"));
        ObservableList<Product> products = FXCollections.observableArrayList();

        products.add(new Product(product.getId(), product.getProductName(), product.getProductModel(), Integer.valueOf(product.getPerPeicePrice()), product.getBarcodeSerialNumber()));
        stockUploadTable.getItems().addAll(products);
    }
}
