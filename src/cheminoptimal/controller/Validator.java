/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cheminoptimal.controller;

import java.util.ArrayList;
import cheminoptimal.model.Arc;
import cheminoptimal.model.Node;

/**
 *
 * @author baroov
 */
public class Validator {
    
    public static String ERROR = "The scene is empty";
    public static boolean  isValid(ArrayList<Node> nodes, boolean  hasDebut, boolean hasFin){
        if(nodes.isEmpty())return false;
        if(hasDebut && hasFin){
            Node debut = nodes.get(0),
                    fin = nodes.get(nodes.size()-1);
            if(!traiterOut(debut))return false;
            if (!traiterIn(fin)) return false;
            nodes.remove(fin);
            nodes.remove(debut);
            for(Node n : nodes){
                if(!traiterIn(n) || !traiterOut(n)){
                    nodes.add(fin);
                    nodes.add(0,debut);
                    return false;
                }
            }
        nodes.add(fin);
        nodes.add(0,debut);
        
        }
        else{
            ERROR = "No End/Begin node.";
            return false;
        }
                    
        return true;
    }
    
    private static boolean traiterOut(Node node){
        if(!node.getOut().isEmpty()){
            for(Arc arc:node.getOut()){
                if(arc.getArcValue().getText().trim().matches("\\d+.{0,1}\\d*")){
                    if(Double.valueOf(arc.getArcValue().getText().trim()) <= 0){
                        ERROR = "The OUT arc's value of "+node.getArret().getText()+" <=0";
                    return false;
                    }
                 
                }
                else{
                    ERROR = "The OUT arc's value of "+node.getArret().getText()+" is Not a Number.";
                    return false;
               
                }
            }
        }else  {
            ERROR = node.getArret().getText()+" does not have any OUT arc.";
            return false;
        }
    return true;
            }
    private static boolean traiterIn(Node node){
   if(!node.getIn().isEmpty()){
            for(Arc arc:node.getIn()){
                if(arc.getArcValue().getText().trim().matches("\\d+.{0,1}\\d*")){
                    if(Double.valueOf(arc.getArcValue().getText().trim()) <= 0){
                        ERROR = "The IN arc's value of "+node.getArret().getText()+" <=0";
                    return false;
                    }
                 
                }
                else{
                    ERROR = "The IN arc's value of "+node.getArret().getText()+" is Not a Number.";
                    return false;
               
                }
            }
        }else  {
            ERROR = node.getArret().getText()+" does not have any IN arc.";
            return false;
   }
    return true;
    }
    
}
