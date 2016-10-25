/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.config.Config;
import eu.smartsocietyproject.pf.Application;
import eu.smartsocietyproject.pf.CollectiveKind;
import eu.smartsocietyproject.pf.TaskDefinition;
import eu.smartsocietyproject.pf.TaskRequest;
import eu.smartsocietyproject.pf.TaskRunner;
import java.util.Set;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class DemoApplication extends Application {

    @Override
    public String getApplicationId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void init(Config config) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<CollectiveKind> listCollectiveKinds() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TaskRequest createTaskRequest(TaskDefinition definition) {
        return new DemoTaskRequest(definition);
    }

    @Override
    public TaskRunner createTaskRunner(TaskRequest request) {
        if(request instanceof DemoTaskRequest) {
            return new DemoTaskRunner((DemoTaskRequest)request);
        }
        throw new UnsupportedOperationException("Not supported request type!");
    }
    
}
