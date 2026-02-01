package controllers.OrderPanelsControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuOrderController {
    @FXML
    private Button addorderBut;
    @FXML
    private Button editorderBut;
    @FXML
    private Button orderlistBut;
    @FXML
    private Button logoutBut;


    public void addorderbutAction(ActionEvent event) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/OrderMasterPanel/MorderADDPanel.fxml"));

            Stage stage = new Stage();
            stage.setTitle("add");
            stage.setScene(new Scene(root));
            stage.show();


            Stage mainStage=(Stage) addorderBut.getScene().getWindow();
            mainStage.hide();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void editorderbutAction(ActionEvent event) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/OrderMasterPanel/MorderEditPanel.fxml"));

            Stage stage = new Stage();
            stage.setTitle("edit");
            stage.setScene(new Scene(root));
            stage.show();


            Stage mainStage=(Stage) editorderBut.getScene().getWindow();
            mainStage.hide();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void orderlistAction(ActionEvent event) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/OrderMasterPanel/OrdersListPanel.fxml"));

            Stage stage = new Stage();
            stage.setTitle("orders");
            stage.setScene(new Scene(root));
            stage.show();


            Stage mainStage=(Stage) orderlistBut.getScene().getWindow();
            mainStage.hide();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public void logoutbutAction(ActionEvent event) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/WorkersPanels/LoginPanel.fxml"));

            Stage stage = new Stage();
            stage.setTitle("exit");
            stage.setScene(new Scene(root));
            stage.show();


            Stage mainStage=(Stage) logoutBut.getScene().getWindow();
            mainStage.hide();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
