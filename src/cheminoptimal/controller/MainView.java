/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cheminoptimal.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;
import cheminoptimal.Main;
import static cheminoptimal.controller.Validator.ERROR;
import cheminoptimal.model.Arc;
import cheminoptimal.model.Node;
import cheminoptimal.model.TableauFord;
import java.util.Objects;

/**
 * FXML Controller class
 *
 * @author baroov
 */
public class MainView implements Initializable {
    @FXML
    private AnchorPane workspacePane;
    @FXML
    private Button graphBtn;
    @FXML
    private Button beginGraphBtn;
    @FXML
    private Button endGraphBtn;
    @FXML
    private Button arcBtn;
    
    private RessourceManager rsm;
    private final int DEBUT=0,FIN=1,SIMPLE=2;
    private ImageView cursor;
    private boolean hasDebut = false,
                    hasFin = false,
                    isXiSelected = false,
                    isArcGraphSelected = false,
                    isDebutSelected = false,
                    isFinSelected = false;
    
    private ArrayList<Parent> nodes;
    private ArrayList<Node> controllers;
    private ArrayList<Arc> arcs;

    private Stage aboutWindow;
    private Parent nodeClickedFirst, nodeClickedLast;
    private int clickCount = 0;
    private Line line;
    private double posX, posY;
    private final Image xiCursor = new Image(Main.class.getResource("img/button.png").toString())
                    ,x1Cursor = new Image(Main.class.getResource("img/buttonX1Cursor.png").toString())
                    ,xnCursor = new Image(Main.class.getResource("img/buttonXnCursor.png").toString());
    @FXML
    private TableView<TableauFord> resultTable;
    @FXML
    private TableColumn<TableauFord,String> column_I;
    @FXML
    private TableColumn<TableauFord, String> column_J;
    @FXML
    private TableColumn<TableauFord, String> lamdaJ_I;
    @FXML
    private TableColumn<TableauFord, String> vXi_vXj;
    @FXML
    private TableColumn<TableauFord, String> lamdaJ;
    
    private ObservableList<TableauFord> content;
    
    String LAMDA = "λ" , INFINI = "∞";
    @FXML
    private Button exportBtn;
    @FXML
    private Button importBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private Button launchBtn;
    @FXML
    private Button validateBtn;
    @FXML
    private Button clearSceneBtn;
    @FXML
    private Button aboutBtn;
    
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        exportBtn.setTooltip(new Tooltip("Export as xml"));
        importBtn.setTooltip(new Tooltip("Import from xml"));
        saveBtn.setTooltip(new Tooltip("Save"));
        launchBtn.setTooltip(new Tooltip("Find optimal path"));
        validateBtn.setTooltip(new Tooltip("Validate graph"));
        clearSceneBtn.setTooltip(new Tooltip("Clear workspace"));
        aboutBtn.setTooltip(new Tooltip("About"));
        
        content = resultTable.getItems();
        
        column_I.setCellValueFactory(new PropertyValueFactory("i"));
        column_J.setCellValueFactory(new PropertyValueFactory("j"));
        
        lamdaJ_I.setText(LAMDA+"j - "+LAMDA+"i");
        lamdaJ_I.setCellValueFactory(new PropertyValueFactory("lambdaJ_lambdaI"));
        
        vXi_vXj.setText("v(Xi , Xj)");
        vXi_vXj.setCellValueFactory(new PropertyValueFactory("vxI_xJ"));
        
        lamdaJ.setText(LAMDA+"j");
        lamdaJ.setCellValueFactory(new PropertyValueFactory("lambdaJ"));
        
        rsm = RessourceManager.getInstance();
        rsm.setWorkspace(workspacePane);
        aboutWindow = new Stage();
        aboutWindow.setResizable(false);
        try {
            
            FXMLLoader loader = new FXMLLoader(new URL(Main.class.getResource("view/About.fxml").toString()));
            aboutWindow.setScene(new Scene((Parent) loader.load()));
            aboutWindow.initModality(Modality.APPLICATION_MODAL);
            aboutWindow.setTitle("About");
            ((About)loader.getController()).setStage(aboutWindow);
            
        } catch (MalformedURLException ex) {
        } catch (IOException ex) {
        }
        nodes = new ArrayList<>();
        controllers = new ArrayList<>();
        arcs = new ArrayList<>();
        cursor = new ImageView();
        line = new Line();
        line.setVisible(false);
        workspacePane.getChildren().addAll(line,cursor);
    }    

    @FXML
    private void createNode(MouseEvent event) {
        if(isXiSelected){
            drawNode(event, SIMPLE);
        }
        
        else if(isDebutSelected){
            drawNode(event, DEBUT);
        }
        else if(isFinSelected){
            drawNode(event, FIN);
        }

    }
    
    private void changCursor(int type){
    cursor.setVisible(true);
   switch(type){
       case DEBUT:cursor.setImage(x1Cursor);break;
       case FIN:cursor.setImage(xnCursor);break;
       case SIMPLE:cursor.setImage(xiCursor);break;
       default:cursor.setVisible(false);
   }
 
    }
    private void drawTmpLine(MouseEvent event){
        if(clickCount == 1){
            nodeClickedFirst = ((Button)event.getSource()).getParent();
            if(controllers.get(nodes.indexOf(nodeClickedFirst)).getOut()!= null){
            line.setStartX(((Button)event.getSource()).getParent().getLayoutX()+30);
            line.setStartY(((Button)event.getSource()).getParent().getLayoutY()+45);
            line.setVisible(true);
            }
            else clickCount--;
            
            }
            else if(clickCount == 2 ){
            nodeClickedLast = ((Button)event.getSource()).getParent();
            if(nodeClickedFirst!=nodeClickedLast && controllers.get(nodes.indexOf(nodeClickedLast)).getIn()!= null){
            boolean isAlreadyMean = false;
            Point2D debutNodeLast = new Point2D(nodeClickedLast.getLayoutX(),nodeClickedLast.getLayoutY()),
                    debutNodeFirst = new Point2D(nodeClickedFirst.getLayoutX(),nodeClickedFirst.getLayoutY());
            Node n = controllers.get(nodes.indexOf(nodeClickedLast));
                for(Arc a:n.getIn()){
                    if(a.getFin().equals(debutNodeLast) && a.getDebut().equals(debutNodeFirst)){
                        isAlreadyMean = true;
                    clickCount--;
                    break;
                    }
                }
            if(!isAlreadyMean){
                final Arc arc = new Arc(debutNodeFirst,debutNodeLast,workspacePane);
                    arc.getLine().setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

                @Override
                public void handle(ContextMenuEvent event) {
                    
                    removeArcComponents(arc);
                }

            });
            controllers.get(nodes.indexOf(nodeClickedFirst)).addOutArc(arc);
            controllers.get(nodes.indexOf(nodeClickedLast)).addInArc(arc);
            arcs.add(arc);
            line.setVisible(false);
            
            clickCount = 0;
            }
            }
            else clickCount--;
            }
    }
    
    
    private void removeArcComponents(Arc arc) {
        
        for(Node n:controllers){
                     if(n.getIn() != null)
                        if(n.getIn().contains(arc))
                            n.removeInArc(arc);
                    if(n.getOut()!= null)  
                        if(n.getOut().contains(arc))
                            n.removeOutArc(arc);
        }
        arcs.remove(arc);
        workspacePane.getChildren().remove(arc.getArrow());
        workspacePane.getChildren().remove(arc.getLine());
        workspacePane.getChildren().remove(arc.getArcValue());
        System.gc();
                }
    private void drawNode(MouseEvent event, int type){
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/Node.fxml"));
            final Parent nodeView = (Parent)loader.load();
            final Node node = loader.getController();
            nodeView.setLayoutX(event.getX()-25);
            nodeView.setLayoutY(event.getY()-35);
            node.getArret().setOnMousePressed(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {
                    posX = event.getX();
                    posY = event.getY();
                }
            }
           );
            
           node.getArret().setOnMouseReleased(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {
                    clickCount++;
                    if (clickCount > 2)clickCount=0;
                if(isArcGraphSelected){
                    drawTmpLine(event);
                }
                }
            });
           node.getArret().setOnMouseDragged(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {
                    if(!isArcGraphSelected){
                        double xdrag = nodeView.getLayoutX() + (event.getX() - posX),
                    ydrag = nodeView.getLayoutY()+ (event.getY() - posY);
                    nodeView.setLayoutX(xdrag);
                    nodeView.setLayoutY(ydrag);
                    if(node.getIn() != null){
                        for(Arc a:node.getIn()){
                            a.setFin(new Point2D(xdrag, ydrag));
                        }
                    }
                    if(node.getOut() != null){
                        for(Arc a:node.getOut()){
                            a.setDebut(new Point2D(xdrag, ydrag));
                        }
                    }
                }
                }
            });
            nodeView.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
           
                @Override
                public void handle(ContextMenuEvent event) {
                    if((isXiSelected || beginGraphBtn.isDisabled() || endGraphBtn.isDisable()) && !isArcGraphSelected)deleteNode((Parent)event.getSource());
                }
            });
            
            switch(type){
                case FIN:{
                    nodes.add(nodeView);
                    node.setName("Xn");
                    node.setFin();
                    controllers.add(node);
                    hasFin = true;
                    isFinSelected = false;
                    endGraphBtn.setDisable(true);
                    cursor.setVisible(false);
                    
                    }break;
                case DEBUT:{
                     node.setName("X1");
                     node.setDebut();
                     controllers.add(0,node);
                     nodes.add(0,nodeView);
                     hasDebut = true;
                     isDebutSelected = false;
                     beginGraphBtn.setDisable(true);
                     cursor.setVisible(false);
                    }break;
                default:{
                    int x = nodes.size()+2;
                    if(hasFin){
                    nodes.add(nodes.size()-1,nodeView);
                    x-=1;
                    controllers.add(controllers.size()-1,node);
                    }
                    else{
                    nodes.add(nodeView);
                    controllers.add(node);
                    }
                    if(hasDebut)x-=1;
                    node.setName("X"+x);
                 }
            }
            
            
            workspacePane.getChildren().add(nodeView);
            } catch (IOException ex) {
            Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
                }
    }
    private void deleteNode(Parent n){
    Node node = controllers.get(nodes.indexOf(n));
     if(isDebut(node)){
         hasDebut = false;
         beginGraphBtn.setDisable(false);
         beginGraphBtn.setStyle("-fx-background-image:url('"+Main.class.getResource("img/buttonX1.png").toString()+"')");
     }
     else if(isFin(node)){
         hasFin = false;
         endGraphBtn.setDisable(false);
         endGraphBtn.setStyle("-fx-background-image:url('"+Main.class.getResource("img/buttonXn.png").toString()+"')");
     }
     controllers.remove(node);
     nodes.remove(n);
     
     workspacePane.getChildren().remove(n);
     int i = 2;
     for(Node nod:controllers){
         if(!isDebut(nod) && !isFin(nod)){
                nod.setName("X"+i);
                i++;
         }
     }
     if(node.getIn() != null)
         for(Arc a:node.getIn()){
            removeArcComponents(a);
     }
     if(node.getOut() != null)for(Arc a:node.getOut()){
        removeArcComponents(a);
     }
    }
    
    private boolean isDebut(Node nod){
    return (nod.getIn()==null);
    }
    private boolean isFin(Node nod){
    return (nod.getOut()==null);
    }
    
    @FXML
    private void invertXiSelection(MouseEvent event) {
    if(isArcGraphSelected)invertArcSelection(null);
    isXiSelected = !isXiSelected; 
    if(isXiSelected){
        changCursor(SIMPLE);
        graphBtn.setStyle("-fx-background-image:url('"+Main.class.getResource("img/buttonXiAfter.png").toString()+"')");
        clickCount = 0;
    }
    else {
        changCursor(9);
        graphBtn.setStyle("-fx-background-image:url('"+Main.class.getResource("img/buttonXi.png").toString()+"')");
    }
    }

    private void changeX1Cursor(MouseEvent event) {
    if(isXiSelected)invertXiSelection(null);
    if(isArcGraphSelected)invertArcSelection(null);
        changCursor(DEBUT);
    isDebutSelected = true;
    }

    private void changeXnCursor(MouseEvent event) {
    if(isXiSelected){
        invertXiSelection(null);
    }
    if(isArcGraphSelected)invertArcSelection(null);
    isFinSelected = true;
        changCursor(FIN);
    }

    @FXML
    private void invertArcSelection(MouseEvent event) {
        if(isXiSelected)invertXiSelection(null);
        isArcGraphSelected = !isArcGraphSelected;
        if(isArcGraphSelected)arcBtn.setStyle("-fx-background-image:url('"+Main.class.getResource("img/buttonArcAfter.png").toString()+"')");
        else {
            arcBtn.setStyle("-fx-background-image:url('"+Main.class.getResource("img/buttonArc.png").toString()+"')");

        }
        line.setVisible(false);
       clickCount = 0;
    }

    private void changeX1orXnCursor() {

        isXiSelected = false;
        graphBtn.setStyle("-fx-background-image:url('"+Main.class.getResource("img/buttonXi.png").toString()+"')");
        
        isArcGraphSelected = false;
        arcBtn.setStyle("-fx-background-image:url('"+Main.class.getResource("img/buttonArc.png").toString()+"')");
        
        if(isFinSelected){
            changCursor(FIN);
            endGraphBtn.setStyle("-fx-background-image:url('"+Main.class.getResource("img/buttonXnAfter.png").toString()+"')");
        }
        else if(isDebutSelected){
            changCursor(DEBUT);
            beginGraphBtn.setStyle("-fx-background-image:url('"+Main.class.getResource("img/buttonXiAfter.png").toString()+"')");
        }
    }

    @FXML
    private void revertX1Cursor(MouseEvent event) {
        changCursor(9);
       isDebutSelected = !isDebutSelected;
       changeX1orXnCursor();
    }

    @FXML 
    private void revertXnCursor(MouseEvent event) {
     changCursor(9);
      isFinSelected = !isFinSelected;
      changeX1orXnCursor();
    }

    @FXML
    private void drawLine(MouseEvent event) {
        line.setEndX(event.getX());
        line.setEndY(event.getY());
        cursor.setLayoutX(event.getX());
        cursor.setLayoutY(event.getY());
    }

    @FXML
    private void findOptimalPath(Event event) {
        if(Validator.isValid(controllers, hasDebut, hasFin)){
             calculate();
            }
        else showError(ERROR);

    }
    
    private void showError(String error){
        Notifications.create()
                .darkStyle()
                .owner(workspacePane)
                    .position(Pos.CENTER)
                        .text(error)
                            .showError();
        
    }
    
    private String getString(double nb){
        if(nb == Double.POSITIVE_INFINITY) return INFINI;       
        return String.format("%.1f", nb).replaceAll(",",".");
    }
    private void calculate(){
        content.clear();
        for(Arc a:arcs){
            a.setCouleur(Color.BLACK);
        }
        controllers.get(0).setLamdaValue(0);
        for(int i = 1; i < controllers.size(); i++){
           controllers.get(i).setLamdaValue(Double.POSITIVE_INFINITY);
           controllers.get(i).unsetMin();
        }
        for(int i = 0; i < controllers.size(); i++){
            Node node = controllers.get(i);
            if(node.getOut() != null){
              for(Arc arc : node.getOut()){
                Node node2 = controllers.get(controllers.indexOf(node)+1);
                for(Node n : controllers){
                    if(n.getIn() != null){
                        if(n.getIn().contains(arc)){
                            node2 = n;
                            break;
                        }
                    }
                }
                double lamda1, lamda2;
                String lamda2Str;
                lamda1 = node.getLamdaValue();
                if(lamda1 == Double.POSITIVE_INFINITY) break;
                lamda2 = node2.getLamdaValue();
                double lamda2_lamda1 = lamda2 - lamda1;
                double arcValue = Double.valueOf(arc.getArcValue().getText());
 
                if(lamda2_lamda1 > arcValue){
                    if(controllers.indexOf(node2) < i){
                        i = controllers.indexOf(node2) - 1;
                        node2.setLambdaValue((controllers.indexOf(node2)+1),(lamda1+arcValue));
                        lamda2Str = getString(lamda1+arcValue);
                        content.add(new TableauFord(getITableauFordValue(node.getArret().getText(),content.size()-1),
                        node2.getArret().getText(),
                            LAMDA+(controllers.indexOf(node2)+1)+" - "+LAMDA+(i+1)+" = "
                                    +getString(lamda2)+" - "+getString(lamda1)+" = "+getString(lamda2_lamda1),
                                arc.getArcValue().getText(),
                                    lamda2Str));
                        break;
                    }
                    node2.setLambdaValue((controllers.indexOf(node2)+1),(lamda1+arcValue));
                    lamda2Str = getString(lamda1+arcValue);  
                }
                else lamda2Str = "";

                content.add(new TableauFord(getITableauFordValue(node.getArret().getText(),content.size()-1),
                    node2.getArret().getText(),
                        LAMDA+(controllers.indexOf(node2)+1)+" - "+LAMDA+(i+1)+" = "
                            +getString(lamda2)+" - "+getString(lamda1)+" = "+getString(lamda2_lamda1),
                                arc.getArcValue().getText(),
                                    lamda2Str));
              }
            }
        }
        traceMinPath(controllers.get(controllers.size()-1));
    }

   
    private void traceMinPath(Node depart){

    if(depart!=null)  
      if(!isDebut(depart) ){
        for(Arc in:depart.getIn()){
            for(Node n:controllers){
                if(!isFin(n))
                for(Arc out:n.getOut()){
                    if(in.equals(out)){
                       if(Objects.equals(Double.valueOf(getString((depart.getLamdaValue()-Double.valueOf(in.getArcValue().getText())))), Double.valueOf(getString(n.getLamdaValue())))){
                           depart.setMin();
                           out.setCouleur(Color.ORANGERED);
                           traceMinPath(n);
                       } else {
                       }
                       
                }    
            
            }
            }
        }        
    }
     else depart.setMin();
    }
    private String getITableauFordValue(String s,int index){
    if(index >= 0){
        if(content.get(index).getI().equals(""))
            return getITableauFordValue(s, --index);
        else if(content.get(index).getI().equals(s))
            return "";
        else return s;
    }
    return s;
    }
    @FXML
    private void validate(Event event) {
        if(Validator.isValid(controllers, hasDebut, hasFin)){
             showError("No error detected!");
            }
        else showError(ERROR);
    }

    @FXML
    private void exportScene(ActionEvent event) {
      rsm.setFile(null);
        rsm.export(controllers, nodes,arcs);
    }

    @FXML
    private void loadScene(ActionEvent event) {
    if(rsm.load(controllers, nodes, arcs)){
    workspacePane.getChildren().add(line);
    workspacePane.getChildren().add(cursor);
    if(isXiSelected)cursor.setVisible(true);
    addEventToAllComponent();
    Notifications.create()
                .darkStyle()
                .owner(workspacePane)
                    .position(Pos.TOP_RIGHT)
                        .text("Scene loaded from "+rsm.getFile().getPath())
                            .showInformation();
    }
    }
    
   private void clearWorkSpace(){
    rsm.setFile(null);
    workspacePane.getChildren().clear();
    line.setVisible(false);
    cursor.setVisible(false);
    workspacePane.getChildren().add(line);
    workspacePane.getChildren().add(cursor);
    content.clear();
    System.gc();
   }
   
    private void addEventToAllComponent(){
        clickCount = 0;
    for(final Node node:controllers){
        if(isDebut(node))hasDebut = true;
        else if(isFin(node))hasFin = true;
        
    node.getArret().setOnMousePressed(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {
                    posX = event.getX();
                    posY = event.getY();
                }
            }
           );
            
           node.getArret().setOnMouseReleased(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {
                    clickCount++;
                    if (clickCount > 2)clickCount=0;
                if(isArcGraphSelected){
                    drawTmpLine(event);
                }
                }
            });
           final Parent nodeView = nodes.get(controllers.indexOf(node));
           node.getArret().setOnMouseDragged(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {
                    if(!isArcGraphSelected){
                        double xdrag = nodeView.getLayoutX() + (event.getX() - posX),
                    ydrag = nodeView.getLayoutY()+ (event.getY() - posY);
                    nodeView.setLayoutX(xdrag);
                    nodeView.setLayoutY(ydrag);
                    if(node.getIn() != null){
                        for(Arc a:node.getIn()){
                            a.setFin(new Point2D(xdrag, ydrag));
                        }
                    }
                    
                    if(node.getOut() != null){
                        for(Arc a:node.getOut()){
                            a.setDebut(new Point2D(xdrag, ydrag));
                        }
                    }
                }
                }
            });
            nodeView.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
           
                @Override
                public void handle(ContextMenuEvent event) {
                    if((isXiSelected || beginGraphBtn.isDisabled() || endGraphBtn.isDisable()) && !isArcGraphSelected)deleteNode((Parent)event.getSource());
                }
            });

    }
    for(final Arc arc:arcs){
      arc.getLine().setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

                @Override
                public void handle(ContextMenuEvent event) {
                    removeArcComponents(arc);
                }

            });

    }
        endGraphBtn.setDisable(hasFin);
        beginGraphBtn.setDisable(hasDebut);
    }

    @FXML
    private void saveScene(ActionEvent event) {
        rsm.export(controllers, nodes,arcs);
    }

    @FXML
    private void clearScene(ActionEvent event) {
        clearWorkSpace();
        beginGraphBtn.setDisable(false);
        endGraphBtn.setDisable(false);
        hasDebut = false;
        hasFin = false;
        isXiSelected = false;
        graphBtn.setDisable(false);
        isArcGraphSelected = false;
        arcBtn.setDisable(false);
        endGraphBtn.setStyle("-fx-background-image:url('"+Main.class.getResource("img/buttonXn.png").toString()+"')");
        beginGraphBtn.setStyle("-fx-background-image:url('"+Main.class.getResource("img/buttonX1.png").toString()+"')");
        arcs.clear();
        controllers.clear();
        nodes.clear();
        System.gc();
    }

    @FXML
    private void showAbout(ActionEvent event) {
        aboutWindow.showAndWait();
    }

}
