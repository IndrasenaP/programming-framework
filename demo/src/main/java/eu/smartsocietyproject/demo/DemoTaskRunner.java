/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.demo;

import com.fasterxml.jackson.databind.JsonNode;
import eu.smartsocietyproject.pf.TaskRunner;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class DemoTaskRunner implements TaskRunner {
    
    private final DemoTaskRequest request;
    
    public DemoTaskRunner(DemoTaskRequest request) {
        this.request = request;
    }

    //todo-sv: what is this for
    public JsonNode getStateDescription() {
        return null;
    }

    public void run() {
        System.out.println("This would go to google: " 
                + this.request.getRequest());
    }
    
}
