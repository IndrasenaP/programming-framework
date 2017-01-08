/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.scenario2.helper;

import eu.smartsocietyproject.pf.TaskResult;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class RQATaskResult extends TaskResult {
    
    private String result = "";
    private double qos = 0;
    private double qosStep = 0.25;

    @Override
    public boolean isQoRGoodEnough() {
        if(qos == -1) {
            return true;
        }
        
        return false;
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
    
    public synchronized void setGoogleResult(String googleRes) {
        if(qos < 0) { 
            return;
        }
        this.result += "Google:" 
                + System.getProperty("line.separator") 
                + googleRes
                + System.getProperty("line.separator")
                + System.getProperty("line.separator");
        this.qos += qosStep;
    }
    
    public synchronized void setHumanResult(String res) {
        if(qos < 0) { 
            return;
        }
        this.result += "Human opinion:" 
                + System.getProperty("line.separator")
                + res
                + System.getProperty("line.separator")
                + System.getProperty("line.separator");
        this.qos += qosStep;
    }
    
    public synchronized void setOrchestratorsChoice(String res) {
        this.qos = -1;
        this.result = res;
    }
    
}
