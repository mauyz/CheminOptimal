/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cheminoptimal;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javax.swing.JOptionPane;

/**s
 *
 * @author baroov
 */
public  class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        String version = System.getProperty("java.version");
        if(Double.valueOf(version.substring(0, version.indexOf(".")+2))< 1.8){
            JOptionPane.showMessageDialog(null, "Please, install java version >=1.8!", "Version Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
            
        Parent p =  (Parent) FXMLLoader.load(Main.class.getResource("view/ROInterface.fxml"));
        primaryStage.setTitle("Chemin Optimal");
        primaryStage.setScene(new Scene(p));
        primaryStage.getIcons().add(new Image(Main.class.getResource("img/icone.png").toString()));
        primaryStage.show();
        
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
