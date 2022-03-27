/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cheminoptimal.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author baroov
 */
public class About implements Initializable {
    @FXML
    private Button closeBtn;
    private Stage s;
    static Application app;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        app = new Application() {

            @Override
            public void start(Stage primaryStage) throws Exception {
         //       throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    
    }    

    public void setStage(Stage stage){
    this.s = stage;
    }
    @FXML
    private void closeAbout(ActionEvent event) {
      s.close();
    }

    @FXML
    private void gotoBrvMail(ActionEvent event) throws MalformedURLException, IOException {
       app.getHostServices().showDocument("mailto:barouvheritiana@gmail.com");
    }

    @FXML
    private void gotoMauyzMail(ActionEvent event) {
        app.getHostServices().showDocument("mailto:tsiorymauyz@gmail.com");
    }

    @FXML
    private void gotoChoWebSite(ActionEvent event) {
        app.getHostServices().showDocument("https://cheminoptimal.sourceforge.net");
    }
    
}
