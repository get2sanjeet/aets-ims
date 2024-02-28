package main.java.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Author: Afif Al Mamun
 * Written on: 10-Jul-18
 * Project: TeslaRentalInventory
 **/
public class PromptDialogController {
    /**
     * Constructor will pop up a new stage which will contain
     * type of error/notification(header) and its details.
     * @param header : Prompt headline
     * @param error : Description message of prompt
     */
    public PromptDialogController(String header, String error) {

        Stage stg = new Stage();
        stg.setAlwaysOnTop(true);

        //Modality is so that this window must be interacted before others
        stg.initModality(Modality.APPLICATION_MODAL);
        stg.setResizable(false);

        try {

            Parent root = FXMLLoader.load(getClass().getResource("/main/resources/view/dialog.fxml"));
            Scene s = new Scene(root);

            //Getting useful nodes from FXML to set error report
            Label lblHeader = (Label) root.lookup("#lblHeader");
            JFXTextArea txtError = (JFXTextArea) root.lookup("#txtError");
            JFXButton btnClose = (JFXButton) root.lookup("#btnClose");

            lblHeader.setText(header);
            txtError.setText(error);

            //Setting close button event
            btnClose.setOnAction(event -> {
                stg.hide();
            });

            stg.setScene(s);
            stg.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showPopup(Scene scene){
        // cpopupreate a popup
        Popup popup = new Popup();
        Label label = new Label("RTC-14562 saved successfully !!");
        label.setStyle("-fx-text-fill:WHITE;-fx-alignment: CENTER;-fx-font-size: 18px;");
        //label.setOpacity(0.8);
        popup.setAutoHide(true);
        popup.setX(480);
        popup.setY(200);
        TilePane tilePane = new TilePane();
        /*tilePane.setLayoutX(550);
        tilePane.setLayoutY(100);*/
        tilePane.setPrefHeight(50);
        tilePane.setPrefWidth(400);
        tilePane.setOpacity(0.8);
        tilePane.setStyle("-fx-background-color: #163E59;-fx-alignment: CENTER");

        Rectangle tick = new Rectangle();
        tick.setHeight(25);
        tick.setWidth(25);
        tick.setFill(new ImagePattern(new Image(getClass().getResourceAsStream("/main/resources/icons/tick.png"))));

        HBox hBox = new HBox();
        hBox.getChildren().add(tick);
        Label gap = new Label();gap.setPrefWidth(15);
        hBox.getChildren().add(gap);
        hBox.getChildren().add(label);
        tilePane.getChildren().add(hBox);

        popup.getContent().add(tilePane);
        if (!popup.isShowing())
            popup.show(scene.getWindow());
        else
            popup.hide();
}
}
