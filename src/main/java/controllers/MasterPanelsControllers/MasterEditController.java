package controllers.MasterPanelsControllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.io.IOException;

public class MasterEditController {
    @FXML
    private Button saveBut;
    @FXML
    private Button menuBut;



    public void savebutAction(ActionEvent event)throws Exception {

    }


    public void menubutAction(ActionEvent event)throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/OrderMasterPanel/OrderMasterMenuPanel.fxml"));

            Stage stage = new Stage();
            stage.setTitle("menuPanel");
            stage.setScene(new Scene(root));
            stage.show();


            Stage mainStage = (Stage) menuBut.getScene().getWindow();
            mainStage.hide();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}


