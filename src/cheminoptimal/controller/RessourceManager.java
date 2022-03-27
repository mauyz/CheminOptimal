/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cheminoptimal.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.controlsfx.control.Notifications;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import cheminoptimal.Main;
import cheminoptimal.model.Arc;
import cheminoptimal.model.Node;

/**
 *
 * @author baroov
 */
public class RessourceManager {
private static RessourceManager ressourceManager;
private File file;
private final DocumentBuilderFactory dbf;
private final FileChooser fc;
private AnchorPane workspace;


    public static RessourceManager getInstance(){
    return (ressourceManager==null)?ressourceManager=new RessourceManager():ressourceManager;
    }
    private RessourceManager() {
            dbf = DocumentBuilderFactory.newInstance();
            fc = new FileChooser();
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setWorkspace(AnchorPane workspace) {
        this.workspace = workspace;
    }
    
    public boolean load(ArrayList<Node> nodes, ArrayList<Parent> graphnodes,ArrayList<Arc> arcs){
        File newFile = fc.showOpenDialog(null);
        if(newFile != null){
            
            try {
             nodes.clear();
             graphnodes.clear();
             arcs.clear();
             workspace.getChildren().clear();
                DocumentBuilder db = dbf.newDocumentBuilder();
                file = newFile;
                Document doc = db.parse(file);
                Element root = doc.getDocumentElement();
                NodeList xmlNodes = root.getElementsByTagName("Node");
                NodeList xmlArcs = root.getElementsByTagName("Arc");
                if(xmlArcs != null && xmlArcs.getLength() > 0){
                    for(int i = 0; i < xmlArcs.getLength(); i++){
                    Element xmlArc = (Element) xmlArcs.item(i);
                    Point2D debut = new Point2D(Double.valueOf(getValueAsString(xmlArc,"DebutX"))
                                    , Double.valueOf(getValueAsString(xmlArc,"DebutY"))),
                            fin = new Point2D(Double.valueOf(getValueAsString(xmlArc,"FinX"))
                                    , Double.valueOf(getValueAsString(xmlArc,"FinY")));
                    Arc a = new Arc(debut, fin, workspace);
                    a.getArcValue().setText(getValueAsString(xmlArc,"Value"));
                    arcs.add(a);
				}
                
                }
                if(xmlNodes != null && xmlNodes.getLength() > 0){
                    for(int i = 0; i < xmlNodes.getLength(); i++){
                        Element xmlNode = (Element) xmlNodes.item(i);
                        FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/Node.fxml")); 
                        final Parent nodeView = (Parent)loader.load();
                        nodeView.setLayoutX(Double.valueOf(getValueAsString(xmlNode, "LayoutX")));
                        nodeView.setLayoutY(Double.valueOf(getValueAsString(xmlNode, "LayoutY")));                            
                        
                        final Node node = loader.getController();
                        node.setName(getValueAsString(xmlNode, "Name"));
                        String pos = getValueAsString(xmlNode, "Name").substring(1);
                        if(pos.equals("n"))pos ="0";
                        node.setLambdaValue(Integer.valueOf(pos)
                                            ,infiniteStringToDouble(getValueAsString(xmlNode, "Lambda")));
                        switch (xmlNode.getAttribute("Type")) {
                            case "DEBUT":
                                node.setDebut();
                                processOutarcs(arcs, node, xmlNode);
                                
                                break;
                            case "FIN":
                                node.setFin();
                                processInArcs(arcs, node, xmlNode);
                               break;
                            default:
                                processInArcs(arcs, node, xmlNode);
                                processOutarcs(arcs, node, xmlNode);
                            
                                break;
                        }
                    graphnodes.add(nodeView);
                    nodes.add(node);
                    workspace.getChildren().add(nodeView);
                    }
                }
              
                
            } catch (ParserConfigurationException | SAXException | IOException ex) {
                Notifications.create()
                .darkStyle()
                .owner(workspace)
                    .position(Pos.CENTER)
                        .text("Invalid File:"+file.getPath())
                            .showError();
                       return false;
            }
        
        return true;
        }
     return false;
    }

    private static String getValueAsString(Element e, String tagName){
        String value = "0";
        NodeList nl = e.getElementsByTagName(tagName);
        if(nl != null && nl.getLength() > 0){
            Element el = (Element) nl.item(0);
            if(el.hasChildNodes()){
                String tmp = el.getFirstChild().getNodeValue();
                 if(tmp!=null)       
                value = tmp;
                        }
        }
    return value;
    }
    private void processInArcs(ArrayList<Arc> arcs, Node node, Element xmlNode){
    NodeList xmlArcsIn = ((Element)xmlNode.getElementsByTagName("ArcsProprety").item(0)).getElementsByTagName("ArcProprety");
    if(xmlArcsIn != null && xmlArcsIn.getLength() > 0){
        for(int i = 0; i < xmlArcsIn.getLength(); i++){
            Element xmlArc = (Element) xmlArcsIn.item(i);
            String indice = xmlArc.getAttribute("indice");
            if(indice.matches("\\d+"))
            node.addInArc(arcs.get(Integer.valueOf(indice)));
        }
    }
    
    }
    private void processOutarcs(ArrayList<Arc> arcs, Node node, Element xmlNode){
    NodeList xmlArcsOut = ((Element)xmlNode.getElementsByTagName("ArcsProprety").item(1)).getElementsByTagName("ArcProprety");
    if(xmlArcsOut != null && xmlArcsOut.getLength() > 0){
        for(int i = 0; i < xmlArcsOut.getLength(); i++){
            Element xmlArc = (Element) xmlArcsOut.item(i);
            String indice = xmlArc.getAttribute("indice");
            if(indice.matches("\\d+"))
            node.addOutArc(arcs.get(Integer.valueOf(indice)));
        }
     }
    }
    
    
    public void export(ArrayList<Node> nodes, ArrayList<Parent> graphnodes,ArrayList<Arc> arcs){
        
    try {
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();
        Element root = doc.createElement("Data");
        doc.appendChild(root);
        
        Element xmlNodes = doc.createElement("Nodes");
        Element xmlArcs = doc.createElement("Arcs");
        
        root.appendChild(xmlNodes);
        root.appendChild(xmlArcs);
        for(Arc a:arcs){
            Element arc = doc.createElement("Arc");
                addChildByTag(doc, arc,"Value" ,a.getArcValue().getText());
                addChildByTag(doc, arc,"DebutX" ,""+a.getDebut().getX());
                addChildByTag(doc, arc,"DebutY" , ""+a.getDebut().getY());
                addChildByTag(doc, arc,"FinX" , ""+a.getFin().getX());
                addChildByTag(doc, arc,"FinY" , ""+a.getFin().getY());
            xmlArcs.appendChild(arc);
        }
        for(Node node:nodes){
             Element xmlNode = doc.createElement("Node"),
                     xmlArcIn = doc.createElement("ArcsProprety"),
                     xmlArcOut = doc.createElement("ArcsProprety");
             xmlArcOut.setAttribute("Type", "OUT");
             xmlArcIn.setAttribute("Type", "IN");
                addChildByTag(doc, xmlNode,"LayoutX" , ""+graphnodes.get(nodes.indexOf(node)).getLayoutX());
                addChildByTag(doc, xmlNode,"LayoutY" , ""+graphnodes.get(nodes.indexOf(node)).getLayoutY());
                addChildByTag(doc, xmlNode,"Lambda" , ""+node.getLamdaValue());
                addChildByTag(doc, xmlNode,"Name" , node.getName());
                     if(node.getIn() != null)
                        for(Arc i:node.getIn()){
                            Element arc = doc.createElement("ArcProprety");
                            arc.setAttribute("indice",""+arcs.indexOf(i));
                             xmlArcIn.appendChild(arc);
                        }
                        
                     if(node.getOut()!= null)
                        for(Arc o:node.getOut()){
                            Element arc = doc.createElement("ArcProprety");
                            arc.setAttribute("indice",""+arcs.indexOf(o));
                             xmlArcOut.appendChild(arc);
                        }
           
           if(isDebut(node))xmlNode.setAttribute("Type","DEBUT" );
           else if(isFin(node))xmlNode.setAttribute("Type", "FIN");
           else xmlNode.setAttribute("Type", "SIMPLE");
           xmlNode.appendChild(xmlArcIn);
            xmlNode.appendChild(xmlArcOut);
           xmlNodes.appendChild(xmlNode);
        }
        
        save(doc);
    } catch (ParserConfigurationException ex) {
         Notifications.create()
                .darkStyle()
                .owner(workspace)
                    .position(Pos.TOP_RIGHT)
                        .text("Error occured while exporting scene.")
                            .showError();
    }
    }
    
    
    private  void writeToFile(Document doc,File f) throws TransformerConfigurationException, TransformerException{
           
                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer t = tf.newTransformer();
                t.setOutputProperty(OutputKeys.INDENT, "yes");
                DOMSource dOMSource  = new DOMSource(doc);
                StreamResult sr = new StreamResult(f);
                t.transform(dOMSource, sr);
         
    
    }
    private  void addChildByTag(Document doc,Element el, String tagName, String value){
       Element e = doc.createElement(tagName);
        e.appendChild(doc.createTextNode(value));
        el.appendChild(e);
    }
        
    private boolean isDebut(Node nod){
    return (nod.getIn()==null);
    }
    private boolean isFin(Node nod){
    return (nod.getOut()==null);
    }

    private void save(Document doc) {
    try {
        if(file != null){
            writeToFile(doc, file);
            Notifications.create()
                .darkStyle()
                .owner(workspace)
                    .position(Pos.TOP_RIGHT)
                        .text("Scene saved to "+file.getPath())
                            .showInformation();
            
        }
        else saveAs(doc);
        
    } catch (TransformerException ex) {
       Notifications.create()
                .darkStyle()
                .owner(workspace)
                    .position(Pos.TOP_RIGHT)
                        .text("Error occured while exporting scene.")
                            .showError();
    }
    }

    private void saveAs(Document doc) {
        File newFile = fc.showSaveDialog(null);
        if(newFile != null){
        file = newFile;
            save(doc);
        }
    }

    private double infiniteStringToDouble(String valueAsString) {
        if(valueAsString.equals("∞") || valueAsString.contains("finity"))
            return Double.POSITIVE_INFINITY;
        return Double.valueOf(valueAsString);
    }
    
}
