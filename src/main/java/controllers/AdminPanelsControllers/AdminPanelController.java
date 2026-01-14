package controllers.AdminPanelsControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminPanelController {
    @FXML
    private Button addBut;
    @FXML
    private Button delBut;
    @FXML
    private Button editPlaceinfBut;
    @FXML
    private Button editUserinfBut;
    @FXML
    private Button menuBut;

    public void addbutAction(ActionEvent event) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/AdminPanels/AddUserPanel.fxml"));

            Stage stage = new Stage();
            stage.setTitle("AddUserPanel");
            stage.setScene(new Scene(root));
            stage.show();


            Stage mainStage = (Stage) addBut.getScene().getWindow();
            mainStage.hide();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        public void delbutAction (ActionEvent event) throws Exception {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/view/AdminPanels/DelleteUserPanel.fxml"));

                Stage stage = new Stage();
                stage.setTitle("delUserPanel");
                stage.setScene(new Scene(root));
                stage.show();


                Stage mainStage = (Stage) delBut.getScene().getWindow();
                mainStage.hide();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
            public void editPlaceinfoButAction (ActionEvent event) throws Exception {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/view/AdminPanels/EditPlacePanel.fxml"));

                Stage stage = new Stage();
                stage.setTitle("EditPlacePanel");
                stage.setScene(new Scene(root));
                stage.show();


                Stage mainStage = (Stage) editPlaceinfBut.getScene().getWindow();
                mainStage.hide();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
                public void editUserbutAction (ActionEvent event) throws Exception {
                    try {
                        Parent root = FXMLLoader.load(getClass().getResource("/view/AdminPanels/EditInfoPanel.fxml"));

                        Stage stage = new Stage();
                        stage.setTitle("AddUserPanel");
                        stage.setScene(new Scene(root));
                        stage.show();


                        Stage mainStage = (Stage) editUserinfBut.getScene().getWindow();
                        mainStage.hide();


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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


