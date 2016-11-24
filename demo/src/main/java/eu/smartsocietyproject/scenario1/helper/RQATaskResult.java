/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.scenario1.helper;

import eu.smartsocietyproject.pf.TaskResult;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class RQATaskResult extends TaskResult {
    
    private String result = "";
    private double qos = 0;

    @Override
    public boolean isQoRGoodEnough() {
        if(qos < 0.5) {
            return false;
        }
        
        return true;
    }

    @Override
    public double QoR() {
        if(qos > 1) {
            return 1;
        }
        
        return qos;
    }

    @Override
    public String getResult() {
        return result;
    }
    
    public void setGoogleResult(String googleRes) {
        this.result += "Google:" 
                + System.getProperty("line.separator") 
                + googleRes
                + System.getProperty("line.separator");
        this.qos += 0.5;
    }
    
    public void setHumanResult(String res) {
        this.result += "Human opinion:" 
                + System.getProperty("line.separator")
                + res
                + System.getProperty("line.separator");
        this.qos += 0.5;
    }
    
}
