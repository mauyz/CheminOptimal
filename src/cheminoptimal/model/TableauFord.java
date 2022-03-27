/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cheminoptimal.model;

/**
 *
 * @author baroov
 */

public class TableauFord {

    private final  String i,j,lambdaJ_lambdaI,vxI_xJ, lambdaJ;

    public TableauFord(String i, String j, String lambdaJ_lambdaI, String vxI_xJ, String lambdaJ) {
        this.i = i;
        this.j = j;
        this.lambdaJ_lambdaI = lambdaJ_lambdaI;
        this.vxI_xJ = vxI_xJ;
        this.lambdaJ = lambdaJ;
    }
    
    public String getI() {
        return i;
    }

    public String getJ() {
        return j;
    }

    public String getLambdaJ_lambdaI() {
        return lambdaJ_lambdaI;
    }

    public String getLambdaJ() {
        return lambdaJ;
    }

    public String getVxI_xJ() {
        return vxI_xJ;
    }
    
}
