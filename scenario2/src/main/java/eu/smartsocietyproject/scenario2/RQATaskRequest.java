/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.scenario2;

import eu.smartsocietyproject.pf.TaskRequest;
import eu.smartsocietyproject.scenario2.helper.RQATaskDefinition;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class RQATaskRequest extends TaskRequest {

    public RQATaskRequest(RQATaskDefinition definition) {
        super(definition, "GoogleRequestTask");
    }

    @Override
    public String getRequest() {
        //since we know that it is a string node
        return getDefinition().getJson().get("question").asText();
    }

    @Override
    public RQATaskDefinition getDefinition() {
        return (RQATaskDefinition)super.getDefinition();
    }
    
    
    
}
