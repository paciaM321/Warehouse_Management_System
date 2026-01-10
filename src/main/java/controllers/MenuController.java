package controllers;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.io.IOException;


public class MenuController {

    @FXML               ///pierwszy wybór
    private Button putbut;

    @FXML                 ///drugi wybor
    private Button orderlistbut;

    @FXML                   ///trzeci wybor
    private Button relockbut;

    @FXML                    ///czwarty wybor
    private Button editmiejscabut;

    @FXML
    private Button adminbut;

    @FXML
    private Button exitbut;

    public void menubut1(ActionEvent event) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/putPanel.fxml"));

            Stage stage = new Stage();
            stage.setTitle("PutPanel");
            stage.setScene(new Scene(root));
            stage.show();


            Stage mainStage=(Stage) putbut.getScene().getWindow();
            mainStage.hide();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void menubut2(ActionEvent event) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/OrdersListPanel.fxml"));

            Stage stage = new Stage();
            stage.setTitle("OrderListPanel");
            stage.setScene(new Scene(root));
            stage.show();


            Stage mainStage=(Stage) orderlistbut.getScene().getWindow();
            mainStage.hide();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void menubut3(ActionEvent event) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/RelockPanel.fxml"));

            Stage stage = new Stage();
            stage.setTitle("RelockPanel");
            stage.setScene(new Scene(root));
            stage.show();


            Stage mainStage=(Stage) relockbut.getScene().getWindow();
            mainStage.hide();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void menubut4(ActionEvent event) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/EditPacePanel.fxml"));

            Stage stage = new Stage();
            stage.setTitle("EditPanel");
            stage.setScene(new Scene(root));
            stage.show();


            Stage mainStage=(Stage) editmiejscabut.getScene().getWindow();
            mainStage.hide();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void menubut5(ActionEvent event) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/AdminPanels/AdminPanel.fxml"));

            Stage stage = new Stage();
            stage.setTitle("AdminPanel");
            stage.setScene(new Scene(root));
            stage.show();


            Stage mainStage=(Stage) adminbut.getScene().getWindow();
            mainStage.hide();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void menubut6(ActionEvent event) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/LoginPanel.fxml"));

            Stage stage = new Stage();
            stage.setTitle("exit");
            stage.setScene(new Scene(root));
            stage.show();


            Stage mainStage=(Stage) exitbut.getScene().getWindow();
            mainStage.hide();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }












