/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cheminoptimal.model;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.QuadCurve;
import javafx.scene.transform.Rotate;

/**
 *
 * @author baroov
 */
public class Arc{
    private final TextField arcValue;
    private Color couleur;
    private Point2D debut,fin;
    private final QuadCurve line ; 
    private final Arrow arrow ;
    public Arc(Point2D debut, Point2D fin,AnchorPane workspace) {
        this.debut = debut;
        this.fin = fin;
      
        arcValue = new TextField("1");
        arcValue.setStyle("-fx-background-color:transparent");
        arcValue.setPrefSize(60,20);
        arcValue.setOnKeyReleased(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                if(!event.getCode().isDigitKey() && event.getCode() != KeyCode.PERIOD && event.getCode() != KeyCode.BACK_SPACE){
                arcValue.deletePreviousChar();
                }
                if(arcValue.getText().trim().matches("\\d+.{0,1}\\d*")){
                arcValue.setStyle("-fx-background-color:transparent");
                }
                else 
               arcValue.setStyle("-fx-background-color:red");
            }
        });
       line = new QuadCurve();
       line.setStrokeWidth(2);
       line.setFill(Color.TRANSPARENT);
       line.setStroke(Color.BLACK);
       arrow = new Arrow(line, 1f, 5,10,0,0,-5,10,0,0);
       update();
       workspace.getChildren().addAll(line,arcValue,arrow);
    }

    public TextField getArcValue() {
        return arcValue;
    }

    public QuadCurve getLine() {
        return line;
    }

    public void setCouleur(Color couleur) {
        this.couleur = couleur;
        line.setStroke(couleur);
        arrow.setStroke(couleur);
        
    }

    public void setDebut(Point2D debut) {
        this.debut = debut;
        update();
    }

    public void setFin(Point2D fin) {
        this.fin = fin;
        update();
    }

    public Arrow getArrow() {
        return arrow;
    }

    public Point2D getFin() {
        return fin;
    }

    public Point2D getDebut() {
        return debut;
    }
    
     private Point2D getMoveTo(Point2D depart, Point2D arrive){
        double x = depart.getX()+5,y = depart.getY()+45;
        if(arrive.getX() > depart.getX())x = depart.getX() + 55;
        if(isPresqueEgal(depart.getX(), arrive.getX())){
              x = depart.getX()+30;
            if(arrive.getY() < depart.getY())
                y = depart.getY()+20;
            else 
                y = depart.getY()+70;
                
        }
        
        return new Point2D(x, y);
    }
     
       private Point2D getTangente(Point2D depart, Point2D arrive){
        double x=depart.getX()+(arrive.getX()-depart.getX())/2,
        y= arrive.getY()+(depart.getY()-arrive.getY())/2;
       
        if(isPresqueEgal(depart.getX(),arrive.getX())){
        x=(depart.getY()>arrive.getY())?arrive.getX():arrive.getX()-20;
        }
        else if(arrive.getX() < depart.getX()){
            if(arrive.getY() > depart.getY())
                y +=20;
            else y -= 20;
        
        }
        if(isYPresqueEgal(depart.getY(), arrive.getY())){
        y = (depart.getX()>arrive.getX())?arrive.getY()+20:arrive.getY()-20;
        }
        
        return new Point2D(x, y);
    }

     private Point2D getArrivee(Point2D depart, Point2D arrive){
        double x = arrive.getX()+5,y = arrive.getY()+45;
        if(arrive.getX() < depart.getX())x = arrive.getX() + 55;
        if(isPresqueEgal(getTangente(depart, arrive).getX(), arrive.getX())){
            x = arrive.getX()+30;
            if(arrive.getY() < depart.getY())
                y = arrive.getY()+70;
            else y = arrive.getY()+20;
        }
        
        return new Point2D(x, y);
    }

   private boolean isPresqueEgal(double xStart,double xEnd){
   return (Math.abs(xEnd - xStart)< 80);
   }
       private boolean isYPresqueEgal(double  yStart, double yEnd){
    return (Math.abs(yEnd - yStart)< 65);
    }
    
    private void update() {
         Point2D start = getMoveTo(debut, fin),
                end = getArrivee(debut, fin),
                tangente = getTangente(start, end); 
         line.setStartX(start.getX());
         line.setStartY(start.getY());
         line.setEndX(end.getX());
         line.setEndY(end.getY());
         line.setControlX(tangente.getX());
         line.setControlY(tangente.getY());
         arcValue.setLayoutX(tangente.getX());
         arcValue.setLayoutY(tangente.getY());
         arrow.update();
    }
    
  
    
      public  class Arrow extends Polygon {

        public double rotate;
        public float t;
        QuadCurve curve;
        Rotate rz;

        public Arrow( QuadCurve curve, float t) {
            super();
            this.curve = curve;
            this.t = t;
            init();
        }

        public Arrow( QuadCurve curve, float t, double... arg0) {
            super(arg0);
            this.curve = curve;
            this.t = t;
            init();
        }

        private void init() {

            setFill(Color.TRANSPARENT);
            setStroke(Color.BLACK);
            setStrokeWidth(1);
            rz = new Rotate();
            {
                rz.setAxis(Rotate.Z_AXIS);
            }
            getTransforms().addAll(rz);

            update();
        }

        public void update() {
            double size = Math.max(curve.getBoundsInLocal().getWidth(), curve.getBoundsInLocal().getHeight());
            double scale = size / 4d;

            Point2D ori = eval(curve, t);
            Point2D tan = multiply(scale,normalize(evalDt(curve, t)));

            setTranslateX(ori.getX());
            setTranslateY(ori.getY());

            double angle = Math.atan2( tan.getY(), tan.getX());

            angle = Math.toDegrees(angle);

            double offset = -90;
            if( t > 0.5)
                offset = +90;

            rz.setAngle(angle + offset);

        }

        private Point2D multiply(double scale,Point2D p){
        return new Point2D(p.getX()*scale ,p.getY()*scale);
        }
        private Point2D normalize(Point2D p){
        double norme = Math.sqrt(p.getX()*p.getX() + p.getY()*p.getY());
        return new Point2D(p.getX()/norme , p.getY()/norme);
        }
          
        private Point2D eval(QuadCurve c, float t){
              Point2D p = new Point2D(Math.pow(1-t,3)*c.getStartX()+
                     3*t*Math.pow(1-t,2)*c.getControlX()+
                     3*(1-t)*t*t*c.getControlX()+
                      Math.pow(t, 3)*c.getEndX(),
                      Math.pow(1-t,3)*c.getStartY()+
                      3*t*Math.pow(1-t, 2)*c.getControlY()+
                      3*(1-t)*t*t*c.getControlY()+
                      Math.pow(t, 3)*c.getEndY());
              return p;
          }

          private Point2D evalDt(QuadCurve c, float t){
              Point2D p=new Point2D(-3*Math.pow(1-t,2)*c.getStartX()+
                      3*(Math.pow(1-t, 2)-2*t*(1-t))*c.getControlX()+
                      3*((1-t)*2*t-t*t)*c.getControlX()+
                      3*Math.pow(t, 2)*c.getEndX(),
                      -3*Math.pow(1-t,2)*c.getStartY()+
                      3*(Math.pow(1-t, 2)-2*t*(1-t))*c.getControlY()+
                      3*((1-t)*2*t-t*t)*c.getControlY()+
                      3*Math.pow(t, 2)*c.getEndY());
              return p;
          }
    }



    
}
