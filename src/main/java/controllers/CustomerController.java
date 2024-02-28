package main.java.controllers;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.java.entitymanager.TransactionManager;
import main.java.models.Customer;
import org.controlsfx.control.textfield.TextFields;
import main.java.models.DBConnection;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Author: Afif Al Mamun
 * Written on: 7/12/2018
 * Project: TeslaRentalInventory
 **/

public class CustomerController implements Initializable {
    @FXML
    private AnchorPane cusTomerPane;
    @FXML
    private AnchorPane customerPane;
    @FXML
    private JFXTextField txtFName;
    @FXML
    private JFXTextField addressOne;
    @FXML
    private JFXTextField addressTwo;
    @FXML
    private JFXTextField addressThree;
    @FXML
    private JFXTextField phone;
    @FXML
    private JFXTextField email;
    @FXML
    private Label memberSince;
    @FXML
    private JFXToggleButton btnEditMode;
    @FXML
    private JFXButton btnPrevEntry;
    @FXML
    private JFXButton btnNextEntry;
    @FXML
    private Label customerID, customerDue, lblSearchResults, lblMode;
    @FXML
    private Label lblPageIndex;
    @FXML
    private JFXTextField txtSearch;
    @FXML
    private JFXButton btnSearch;
    @FXML
    private Circle imgCustomerPhoto;
    @FXML
    private JFXRadioButton radioMale;
    @FXML
    private ToggleGroup gender;
    @FXML
    private JFXRadioButton radioFemale;
    @FXML
    private AnchorPane customerListPane;
    @FXML
    private JFXButton btnLViewAllCustomers, btnGoBack;
    @FXML
    private FontAwesomeIconView btnSeachIcon;
    @FXML
    private JFXButton btnAddNew, btnSave;
    @FXML
    private JFXButton btnPurchases;
    @FXML
    private JFXButton btnRentals, btnDelete;
    @FXML
    private FontAwesomeIconView btnAddIcon;
    @FXML
    private TableView<Customer> tbl;
    @FXML
    private TableColumn<Customer, Integer> columnID;
    @FXML
    private TableColumn<Customer, String> columnFirstName;
    @FXML
    private TableColumn<Customer, String> columnGender;
    @FXML
    private TableColumn<Customer, String> columnAddress;
    @FXML
    private TableColumn<Customer, String> columnPhone;
    @FXML
    private TableColumn<Customer, String> columnEmail;

    private static int recordIndex = 0;
    private static int recordSize = 0;
    public static ObservableList<Customer> customersList = FXCollections.observableArrayList(); //This field will auto set from InitializerController Class
    public static ObservableList<Customer> tempList = FXCollections.observableArrayList(); //Will hold the main list while searching
    public static ArrayList<String> customerNames = new ArrayList<>();
    /**
     * onView field is used to hold the Customer object
     * that is currently loaded on screen
     */
    private Customer onView = null;
    private static boolean searchDone = false;
    /**
     * addFlag will differentiate b/w Adding a new entry
     * and updating an existing entry.
     * True: New Record Entry Mode
     * False: Updating an Existing Entry
     */
    private static boolean addFlag = false;
    private static String imgPath = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(LogInController.loggerAccessLevel.equals("Admin"))
            btnDelete.setDisable(false);

        TextFields.bindAutoCompletion(txtSearch, customerNames); //Auto complete field is set now

        //Setting next entry if any on next button action
        btnNextEntry.setOnAction(event -> {
            onView = customersList.get(++recordIndex);
            recordNavigator();
            lblPageIndex.setText("Showing " + (recordIndex + 1) + " of " + recordSize + " results.");
            if (recordIndex == recordSize - 1)
                btnNextEntry.setDisable(true);
            btnPrevEntry.setDisable(false);

        });

        //Setting previous entry if any on previous button action
        btnPrevEntry.setOnAction(event -> {
            onView = customersList.get(--recordIndex);
            recordNavigator();
            lblPageIndex.setText("Showing " + (recordIndex + 1) + " of " + recordSize + " results.");
            btnNextEntry.setDisable(false);
            if (recordIndex == 0)
                btnPrevEntry.setDisable(true);
        });
        setView();
    }

    // setView() method will set the record list of customers
    // and re-initialize the view
    private void setView() {
        imgPath = null;
        customerListPane.setVisible(false); //Initially customer list view is set as not visible

        recordIndex = 0; //Resetting record index
        recordSize = customersList.size();

        //Tooltip will be activated on Customer's photo if hovered
        Tooltip tooltip = new Tooltip("Double Click to Change Avatar in 'Edit Mode'");
        Tooltip.install(imgCustomerPhoto, tooltip);

        imgCustomerPhoto.setOnMouseClicked(event -> {
            if (btnEditMode.isSelected() && event.getClickCount() == 2) {
                FileChooser fc = new FileChooser();
                fc.setTitle("Choose Photo");

                File imgFile = fc.showOpenDialog(btnEditMode.getScene().getWindow());

                if(imgFile != null) { //This block will be only executed if there is any file chosen
                    imgPath = imgFile.toURI().toString();

                    if(imgPath.contains(".jpg") || imgPath.contains(".png") || imgPath.contains(".gif") ||imgPath.contains(".jpeg")) {
                        ImagePattern gg = new ImagePattern(new Image(imgPath));
                        imgCustomerPhoto.setFill(gg);
                    } else {
                        new PromptDialogController("File Format Error!", "Please select a valid image file. You can select JPG, JPEG, PNG, GIF");
                    }
                }

            }
        });

        btnNextEntry.setDisable(true); //For purpose ;)

        if (recordSize > 0) {
            onView = customersList.get(recordIndex); //Setting value for current record

            //Setting customer default fields
            recordNavigator();

            //Setting page indexer value
            lblPageIndex.setText("Showing " + (recordIndex + 1) + " of " + recordSize + " results.");

            if (recordSize > 1) {
                btnNextEntry.setDisable(false); //Next entry will be enabled if there is more than one entry
            }
        }

        btnPrevEntry.setDisable(true); //Disabling prevButton Initially
    }

    //This method will toggle edit mode on/off in customer layout

    @FXML
    void btnEditModeToggle(ActionEvent event) {
        if (btnEditMode.isSelected()) {
            phone.setEditable(true);
            txtFName.setEditable(true);
            addressOne.setEditable(true);
            addressTwo.setEditable(true);
            addressThree.setEditable(true);
            email.setEditable(true);
        } else {
            btnEditMode.setStyle("");
            phone.setEditable(false);
            txtFName.setEditable(false);
            addressOne.setEditable(false);
            addressTwo.setEditable(false);
            addressThree.setEditable(false);
            email.setEditable(false);
        }
    }

    /**
     * This method will calculate a customer's total amount of due
     * @param id : This is the customer's id whose due we want to calculate
     * @return : Returns the total due of a customer
     */

    private Double setDue(Integer id) {
        Connection connection = DBConnection.getConnection();
        Double purchaseDue = 0.0;
        Double rentalDue = 0.0;
        try {
            // Getting total purchase due of customer
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT SUM(amountDue) FROM purchases WHERE Customers_customerID = ?");
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                purchaseDue += rs.getDouble(1);
            }

            // Getting total rental due of customer
            statement = connection.prepareStatement(
                    "SELECT SUM(amountDue) FROM rentals WHERE Customers_customerID = ?");
            statement.setInt(1, id);

            rs = statement.executeQuery();

            while (rs.next()) {
                rentalDue += rs.getDouble(1);
            }

            return purchaseDue + rentalDue;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * This method will navigate between the customer records.
     * Before calling this method the onView field must be set
     * to the item that is going to be displayed.
     */

    private void recordNavigator() {
        // Resetting fields from the data of onView field
        customerID.setText(String.valueOf(onView.getId()));
        txtFName.setText(onView.getName());
        addressOne.setText(onView.getAddressOne());
        addressTwo.setText(onView.getAddressTwo());
        addressThree.setText(onView.getAddressThree());
        email.setText(onView.getEmail());
        phone.setText(onView.getPhone());
        memberSince.setText(onView.getCreateDate().toString());
        customerDue.setText(Double.valueOf(setDue(onView.getId())).toString() + " $");

        if (onView.getGender().equals("Male"))
            radioMale.setSelected(true);
        else
            radioFemale.setSelected(true);

        // Setting Image..
        // If no image is specified/the image path specified is not loadable/image does not exist anymore
        //      we set the default image
        // Otherwise we load the photo from hard disk
        if (onView.getPhoto() == null) {
            ImagePattern img = new ImagePattern(new Image("/main/resources/icons/user.png"));
            imgCustomerPhoto.setFill(img);
        } else {
            try {
                imgPath = onView.getPhoto();
                File tmpPath = new File(imgPath.replace("file:", ""));

                if(tmpPath.exists()) {
                    ImagePattern img = new ImagePattern(new Image(imgPath));
                    imgCustomerPhoto.setFill(img);
                } else {
                    imgPath = null; // Setting imgPath to null cause no valid image found
                    ImagePattern img = new ImagePattern(new Image("/main/resources/icons/user.png"));
                    imgCustomerPhoto.setFill(img);
                }
            } catch (Exception e) {
                //Fallback photo: this will be applied if photo not found or remove in the directory specified directory
                ImagePattern img = new ImagePattern(new Image("/main/resources/icons/user.png"));
                imgCustomerPhoto.setFill(img); //Setting image
            }
        }
    }

    @FXML
    void showPurchases(ActionEvent event) {
        try {
            CustomerPurchaseListController.customerID = Integer.valueOf(customerID.getText());
            Parent pur = FXMLLoader.load(getClass().getResource("/main/resources/view/customerpurchase.fxml"));
            Scene s = new Scene(pur);
            Stage stg = new Stage();
            stg.setResizable(false);
            stg.setScene(s);
            stg.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void showrentals(ActionEvent event) {
        try {
            CustomersRentalListController.customerID = Integer.valueOf(customerID.getText());
            Parent rent = FXMLLoader.load(getClass().getResource("/main/resources/view/customerrentals.fxml"));
            Scene s = new Scene(rent);
            Stage stg = new Stage();
            stg.setResizable(false);
            stg.setScene(s);
            stg.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void listAllCustomers(ActionEvent event) {
        //cusTomerPane.setVisible(false); //Hiding default customer view
        customerListPane.setVisible(true); //Showing total list
        customerPane.setVisible(false);

        columnID.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnFirstName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        columnPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        columnGender.setCellValueFactory(new PropertyValueFactory<>("gender"));

        tbl.setItems(customersList);

        btnGoBack.setOnAction(e -> {
            customerListPane.setVisible(false);
            customerPane.setVisible(true);
        });

    }

    @FXML
    void btnDelAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setGraphic(new ImageView(this.getClass().getResource("/main/resources/icons/x-button.png").toString()));

        alert.setHeaderText("Do you really want to delete this entry?");
        alert.setContentText("Press OK to confirm, Cancel to go back");

        Optional<ButtonType> result = alert.showAndWait();

        if(result.get() == ButtonType.OK) {
            Connection connection = DBConnection.getConnection();
            try {
                PreparedStatement ps = connection.prepareStatement("DELETE FROM  customers WHERE customerID = "+Integer.valueOf(customerID.getText()));
                ps.executeUpdate();
                reloadRecords();

                new PromptDialogController("Operation Successful.", "Item is deleted from the database. Restart or refresh to see effective result.");
            } catch (SQLException e) {
                if(e.getErrorCode() == 1451) {
                    new PromptDialogController("Constraint Error", "Customer has accounts & may be transactions. You need to delete them first in Admin Panel if you want to delete this entry");
                }

            }
        }
    }

    private void reloadRecords() {
        List<Customer> customers = TransactionManager.getCustomers();
        customersList.addAll(customers);
        setView();
    }

    /**
     * This method search into database with the id of a customer and returns the result
     * @param id: ID of the customer
     * @return: The search result of the query
     */
    private ObservableList<Customer> searchWithID(Integer id) {
        Customer customer = TransactionManager.getCustomerById(id);
        return FXCollections.observableArrayList(customer);
    }

    /**
     * This method search into the customer databse with a string and
     * returns any records that has a match with the search string
     * @param name: Name of the customer
     * @return: List of the result
     */

    private ObservableList<Customer> searchWithName(String name) {
       List<Customer> customers = TransactionManager.getCustomerByLikeName(name);
        return FXCollections.observableArrayList(customers);
    }

    @FXML
    void btnSearchAction(ActionEvent event) {
        // searchDone is a boolean field that will store the searching status
        // True: If the searching process is finished
        //       do -> Reset the records to initial
        // False: In case of a new search
        if (searchDone) {
            searchDone = false;
            lblSearchResults.setVisible(false);
            customersList = tempList; //Reassigning customers List
            recordSize = customersList.size();
            btnSearch.setTooltip(new Tooltip("Search with customers name or id"));
            btnSeachIcon.setGlyphName("SEARCH");
            setView();
        } else {
            ObservableList<Customer> searchResult = FXCollections.observableArrayList();
            try {
                // Checking if input field is a number then searching with ID
                // If parsing of Integer fails then we shall try to search
                // with name instead
                Integer id = Integer.valueOf(txtSearch.getText());
                searchResult = searchWithID(id);
            } catch (NumberFormatException e) {
                String name = txtSearch.getText();
                searchResult = searchWithName(name);
            } finally {
                if (searchResult.size() <= 0) {
                    lblSearchResults.setText("No Results Found!");
                    lblSearchResults.setVisible(true);
                } else {
                    tempList = FXCollections.observableArrayList(customersList);
                    searchDone = true;
                    btnSeachIcon.setGlyphName("CLOSE");
                    btnSearch.setTooltip(new Tooltip("Reset Full List"));
                    customersList = searchResult; //Assigning search result to customerList
                    recordSize = searchResult.size();
                    lblSearchResults.setText(recordSize + " results found!");
                    lblSearchResults.setVisible(true);
                    // Resetting the view on screen with search results
                    setView();
                }
            }
        }
    }

    @FXML
    void btnAddMode(ActionEvent event) {
        if(addFlag) {
            addFlag = false; //Resetting addFlag value.
            btnAddIcon.setGlyphName("PLUS");

            //Enabling other buttons
            btnPrevEntry.setDisable(false);
            btnNextEntry.setDisable(false);
            btnSearch.setDisable(false);
            btnRentals.setDisable(false);
            btnPurchases.setDisable(false);
            btnLViewAllCustomers.setDisable(false);
            btnEditMode.setSelected(false);
            btnDelete.setDisable(false);

            String defColor = "#263238";

            //Changing Focus Color
            txtFName.setUnFocusColor(Color.web(defColor));
            addressOne.setUnFocusColor(Color.web(defColor));
            addressTwo.setUnFocusColor(Color.web(defColor));
            addressThree.setUnFocusColor(Color.web(defColor));
            phone.setUnFocusColor(Color.web(defColor));
            email.setUnFocusColor(Color.web(defColor));

            //Setting Label
            lblMode.setText("Navigation Mode");

            reloadRecords();

            btnEditModeToggle(new ActionEvent());

        } else {
            Connection con = DBConnection.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement("SELECT max(customerID) FROM customers");
                ResultSet rs = ps.executeQuery();

                while(rs.next()) {
                    customerID.setText(Integer.valueOf(rs.getInt(1) + 1).toString());
                }

                addFlag = true; //Setting flag true to enable exit mode
                btnAddIcon.setGlyphName("UNDO"); //Changing glyph

                //Setting Label
                lblMode.setText("Entry Mode");

                //Disabling other buttons
                btnPrevEntry.setDisable(true);
                btnNextEntry.setDisable(true);
                btnRentals.setDisable(true);
                btnPurchases.setDisable(true);
                btnLViewAllCustomers.setDisable(true);
                btnSearch.setDisable(true);
                btnDelete.setDisable(true);
                btnEditMode.setSelected(true);

                //Cleaning fields
                txtFName.setText("");
                addressOne.setText("");
                addressTwo.setText("");
                addressThree.setText("");
                phone.setText("");
                email.setText("");
                memberSince.setText(LocalDate.now().toString());
                ImagePattern img = new ImagePattern(new Image("/main/resources/icons/user.png"));
                imgCustomerPhoto.setFill(img);
                imgPath = null;

                btnEditModeToggle(new ActionEvent()); //Changing mode into entry mode.. all fields will be available to edit

            } catch (SQLException e) {
                new PromptDialogController("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + e.getErrorCode());
            }
        }
    }

    private boolean checkFields() {
        boolean entryFlag = true;
        if (txtFName.getText().equals("")) {
            txtFName.setUnFocusColor(Color.web("red"));
            entryFlag = false;
        }

        if(addressOne.getText().equals("")) {
            addressOne.setUnFocusColor(Color.web("red"));
            entryFlag = false;
        }
        if(addressTwo.getText().equals("")) {
            addressTwo.setUnFocusColor(Color.web("red"));
            entryFlag = false;
        }
        if(addressThree.getText().equals("")) {
            addressThree.setUnFocusColor(Color.web("red"));
            entryFlag = false;
        }

        if(phone.getText().equals("")) {
            phone.setUnFocusColor(Color.web("red"));
            entryFlag = false;
        }

        if(email.getText().equals("")) {
            email.setUnFocusColor(Color.web("red"));
            entryFlag = false;;
        }

        return entryFlag;
    }

    private void addRecordToDatabase() {
        try {
            Customer customer = new Customer(Integer.valueOf(customerID.getText()),
                    txtFName.getText(),
                    addressOne.getText(),
                    addressTwo.getText(),
                    addressThree.getText(),
                    phone.getText(),
                    email.getText(),
                    imgPath,
                    radioMale.isSelected() ? "Male":"Female",
                    Date.valueOf(LocalDate.now()));

            TransactionManager.save(customer);
            new PromptDialogController("Operation Successful!", "New Customer Added!");
            reloadRecords();
        } catch (Exception e) {
            new PromptDialogController("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + e.getMessage());
        }
    }

    private void updateRecord() {
        try {
            Customer customer = new Customer(Integer.valueOf(customerID.getText()),
                    txtFName.getText(),
                    addressOne.getText(),
                    addressTwo.getText(),
                    addressOne.getText(),
                    phone.getText(),
                    email.getText(),
                    imgPath,
                    radioMale.isSelected() ? "Male":"Female",
                    Date.valueOf(LocalDate.now()));

            TransactionManager.updateCustomer(customer);
            new PromptDialogController("Operation Successful!", "The record is updated!");
            reloadRecords();
        } catch (Exception e) {
            e.printStackTrace();
            new PromptDialogController("SQL Error!", "Error occured while executing Query.\nSQL Error Code: " + e.getMessage());
        }
    }

    @FXML
    void btnSaveAction(ActionEvent event) {
        if (addFlag) {
            boolean fieldsNotEmpty = checkFields();
            if(fieldsNotEmpty) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Entry");
                alert.setGraphic(new ImageView(this.getClass().getResource("/main/resources/icons/question (2).png").toString()));

                alert.setHeaderText("Do you want to add this entry?");
                alert.setContentText("Press OK to confirm, Cancel to go back");

                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == ButtonType.OK) {
                    addRecordToDatabase();
                }
            } else {
                JFXSnackbar snackbar = new JFXSnackbar(customerPane);
                snackbar.show("One or more fields are empty!", 3000);
            }
        } else {
            boolean fieldsNotEmpty = checkFields();
            if(fieldsNotEmpty) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Edit");
                alert.setGraphic(new ImageView(this.getClass().getResource("/main/resources/icons/question (2).png").toString()));

                alert.setHeaderText("Do you really want to update this entry?");
                alert.setContentText("Press OK to confirm, Cancel to go back");

                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == ButtonType.OK) {
                    updateRecord();
                }
            }

        }
    }
}