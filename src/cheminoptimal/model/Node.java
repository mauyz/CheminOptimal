/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cheminoptimal.model;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import cheminoptimal.Main;

/**
 * FXML Controller class
 *
 * @author baroov
 */
public class Node implements Initializable {
    @FXML
    private Label lambdaLbl;
    @FXML
    private Button arret;

  
    private ArrayList<Arc> in;
    private ArrayList<Arc> out;
    private Color couleur;
    private double posX, posY;
    @FXML
    private VBox nodeBox;
    
    private double lamdaValue = Double.POSITIVE_INFINITY;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        in = new ArrayList<>();
        out = new ArrayList<>();
       // lambdaLbl.setText("λ = ∞");
    }    

    public void setLamdaValue(double lamdaValue) {
        this.lamdaValue = lamdaValue;
    }
    
    
    public Button getArret() {
        return arret;
    }

    
    public ArrayList<Arc> getIn() {
        return in;
    }

    public ArrayList<Arc> getOut() {
        return out;
    }
    public void addInArc(Arc a){
    in.add(a);
    }
    public void removeInArc(Arc a){
    in.remove(a);
    }
    public void addOutArc(Arc a){
    out.add(a);
    }
    public void removeOutArc(Arc a){
    out.remove(a);
    }
    public void setName(String name){
    arret.setText(name);
    }
    public String getName(){
    return arret.getText();
    }
    public void setLambdaValue(int indice,double value){
    lamdaValue = value;
    if(value == Double.POSITIVE_INFINITY)lambdaLbl.setText("λ"+((indice!=0)?indice:"n")+"="+"∞");
    else lambdaLbl.setText("λ"+((indice!=0)?indice:"n")+"="+String.format("%.1f", value).replaceAll(",", "."));
    }
    
    public void setDebut() {
        this.in = null;
        lamdaValue = 0;
        changeBackground();
    }

    public Label getLambdaLbl() {
        return lambdaLbl;
    }

    
    public void setFin() {
        this.out = null;
    changeBackground();
    }
    
    private void changeBackground(){
     arret.setStyle("-fx-background-image:url('"+Main.class.getResource("img/buttonEndBegin.png").toString()+"')");
    }
    public void setMin(){
        arret.setStyle("-fx-background-image:url('"+Main.class.getResource("img/buttonEndBegin.png").toString()+"');-fx-background-color:transparent;"
                            + "-fx-background-repeat:stretch");
    //ovana min ny couleur
    }
    public void unsetMin(){
    if(in != null && out != null)
    arret.setStyle("-fx-background-image:url('"+Main.class.getResource("img/button.png").toString()+"');-fx-background-color:transparent;"
                            + "-fx-background-repeat:stretch");
    }

    public double getLamdaValue() {
        return lamdaValue;
    }

    
    
}
