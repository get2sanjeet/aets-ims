package main.java.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.java.entitymanager.TransactionManager;
import main.java.models.ProductSpecificationType;
import main.java.models.ProductSpecification;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProductSpecificationController implements Initializable {

    private static final String MODELS_PATTERN = "^[0-9,.-]*$";

    @FXML
    private TableColumn<?, ?> productNameCol;

    @FXML
    private TableColumn<?, ?> productModelCol;

    @FXML
    private TableColumn<?, ?> priceCol;

    @FXML
    private TableView<ProductSpecificationType> productSpectbl;
    @FXML
    private TableColumn<ProductSpecificationType, Integer> typeIDCol;

    @FXML
    private JFXButton btnUpdate;
    @FXML
    private FontAwesomeIconView btnAddIcon;
    @FXML
    private JFXButton btnDelete;
    @FXML
    private Label lblProductID;
    @FXML
    private JFXTextField txtProductName;
    @FXML
    private JFXTextField txtProductPrice;

    @FXML
    private JFXTextField txtProductModels;

    @FXML
    private Circle productIcon;
    private boolean updateMode = false;

    final FileChooser fileChooser = new FileChooser();

    public static List<ProductSpecification> productSpecifications;
    @FXML
    void reload(ActionEvent event) {
        lblProductID.setText("");
        txtProductName.setText("");
        txtProductModels.setText("");
        txtProductPrice.setText("");
        btnAddIcon.setGlyphName("PLUS");
        updateMode = false;
        btnAddIcon.setDisable(true);
        btnUpdate.setDisable(false);
        btnDelete.setDisable(true);
        setTableData();
    }

   /* @FXML
    void validateModel() {
        String model = txtProductModels.getText();
        if (model != null && !model.trim().equals("")) {
            Pattern pattern = Pattern.compile(MODELS_PATTERN, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(model);
            boolean matchFound = matcher.find();
            if (matchFound){
                txtProductModels.setFocusColor(GREEN);
                btnAddIcon.setDisable(false);
                btnUpdate.setDisable(false);
            } else {
                txtProductModels.setFocusColor(RED);
                btnAddIcon.setDisable(true);
                btnUpdate.setDisable(true);
            }
            txtProductModels.setText(setSanitizedData(model));
        } else {
            txtProductModels.setFocusColor(RED);
            btnAddIcon.setDisable(true);
            btnUpdate.setDisable(true);
        }

    }*/

    private String setSanitizedData(String model) {
        if (model == null || model == "") {
            return null;
        }
        model = model.replaceAll("[A-Za-z_@/#&+-,]", "");
        model = model.replaceAll(" ", ",");
        return model;
    }

    @FXML
    void browsePhoto(MouseEvent event) {
        Stage currentStage = (Stage) txtProductModels.getScene().getWindow();
        File file = fileChooser.showOpenDialog(currentStage);
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            productIcon.setFill(new ImagePattern(image));
        }
    }

    private void loadContents() {
        ProductSpecificationType selectedItem = productSpectbl.getSelectionModel().getSelectedItem();
        lblProductID.setText(selectedItem.getProductSpecTypeID().toString());
        txtProductName.setText(selectedItem.getProductSpecTypeName());
        txtProductModels.setText(selectedItem.getProductSpecTypeModels());
        txtProductPrice.setText(String.valueOf(selectedItem.getProductSpecTypePrice()));
    }

    @FXML
    void addOrUpdateItemType() {
        boolean b = validateFields(txtProductModels.getText(), txtProductName.getText(), txtProductPrice.getText());
        if (!b) {
            txtProductModels.setUnFocusColor(Color.web("red"));
            txtProductName.setUnFocusColor(Color.web("red"));
            txtProductPrice.setUnFocusColor(Color.web("red"));
            return;
        }
        ProductSpecification productSpecification = new ProductSpecification();
        productSpecification.setModel(setSanitizedData(txtProductModels.getText()));
        productSpecification.setName(txtProductName.getText());
        productSpecification.setPrice(Integer.parseInt(txtProductPrice.getText()));
        if (updateMode) {
            productSpecification.setId(Integer.parseInt(lblProductID.getText()));
            TransactionManager.update(productSpecification);
        } else {
            TransactionManager.save(productSpecification);
        }
        InitializerController.reloadData(productSpecifications);
        reload(null);
        btnAddIcon.setDisable(true);
        btnUpdate.setDisable(true);
    }

    private boolean validateFields(String model, String name, String price) {
        if (model == null || name == null || price == null) {
            return false;
        }

        if (model.equals("") || name.equals("") || price.equals("")) {
            return false;
        }
        return true;
    }

    @FXML
    void deleteItemType(ActionEvent event) {
        final int deleteId = Integer.parseInt(lblProductID.getText());
        TransactionManager.deleteProductSpecification(deleteId);
        reload(null);
    }

    private void setTableData() {
        typeIDCol.setCellValueFactory(new PropertyValueFactory<>("productSpecTypeID"));
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("productSpecTypeName"));
        productModelCol.setCellValueFactory(new PropertyValueFactory<>("productSpecTypeModels"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("productSpecTypePrice"));
        ObservableList<ProductSpecificationType> productSpecificationTypes = FXCollections.observableArrayList();


        if (productSpecifications.size() > 0){
            productSpecifications.stream().forEach(ps -> productSpecificationTypes.add(new ProductSpecificationType(ps.getId(), ps.getName(), ps.getModel(), Integer.valueOf(ps.getPrice()))));
        }
        productSpectbl.setItems(productSpecificationTypes);
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnAddIcon.setDisable(false);
        btnUpdate.setDisable(false);
        productSpectbl.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2)
            {
                updateMode = true;
                btnAddIcon.setGlyphName("SAVE");
                loadContents();
                btnDelete.setDisable(false);
                btnUpdate.setDisable(false);
            }
        });
        setTableData();
    }
}
