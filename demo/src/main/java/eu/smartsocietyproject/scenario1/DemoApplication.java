/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.scenario1;

import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.config.Config;
import eu.smartsocietyproject.pf.Application;
import eu.smartsocietyproject.pf.ApplicationBasedCollective;
import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.CollectiveKind;
import eu.smartsocietyproject.pf.SmartSocietyApplicationContext;
import eu.smartsocietyproject.pf.TaskDefinition;
import eu.smartsocietyproject.pf.TaskRequest;
import eu.smartsocietyproject.pf.TaskRunner;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.ProvisioningHandler;
import eu.smartsocietyproject.scenario1.helper.RQATaskDefinition;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class DemoApplication extends Application {
    
    private SmartSocietyApplicationContext ctx;
    
    public DemoApplication(SmartSocietyApplicationContext ctx) {
        this.ctx = ctx;
    }

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
        if(definition instanceof RQATaskDefinition) {
            return new DemoTaskRequest((RQATaskDefinition)definition);
        }
        return null;
    }

    @Override
    public TaskRunner createTaskRunner(TaskRequest request) {
        if(request instanceof DemoTaskRequest) {
            return new DemoTaskRunner((DemoTaskRequest)request, ctx);
        }
        throw new UnsupportedOperationException("Not supported request type!");
    }    
}
