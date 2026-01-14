package controllers.WorkersPanelsControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Part;

import java.io.IOException;

public class PutPanelController {
    @FXML
    private TextField part_nrField;
    @FXML
    private TextField locationField;

    @FXML
    private Button saveBut;
    @FXML
    private Button menuBut;




//    public void savebutAction (ActionEvent event) throws Exception {
//        String inputpart_nr = part_nrField.getText();
//        String inputLocation = locationField.getText();
//        Part locatio
//
//    }
    public void menubutAction (ActionEvent event) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/WorkersPanels/menuPanel.fxml"));

            Stage stage = new Stage();
            stage.setTitle("MenuPanel");
            stage.setScene(new Scene(root));
            stage.show();


            Stage mainStage = (Stage) menuBut.getScene().getWindow();
            mainStage.hide();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
