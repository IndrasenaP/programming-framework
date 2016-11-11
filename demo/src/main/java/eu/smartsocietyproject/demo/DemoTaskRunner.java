/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.demo;

import com.fasterxml.jackson.databind.JsonNode;
import eu.smartsocietyproject.demo.handler.RQACompositionHandler;
import eu.smartsocietyproject.demo.handler.RQAExecutionHandler;
import eu.smartsocietyproject.demo.handler.RQANegotiationHandler;
import eu.smartsocietyproject.demo.handler.RQAProvisioningHandler;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.peermanager.query.PeerQuery;
import eu.smartsocietyproject.peermanager.query.QueryOperation;
import eu.smartsocietyproject.peermanager.query.QueryRule;
import eu.smartsocietyproject.pf.ApplicationBasedCollective;
import eu.smartsocietyproject.pf.AttributeType;
import eu.smartsocietyproject.pf.CBTBuilder;
import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.CollectiveBasedTask;
import eu.smartsocietyproject.pf.SmartSocietyApplicationContext;
import eu.smartsocietyproject.pf.TaskFlowDefinition;
import eu.smartsocietyproject.pf.TaskResult;
import eu.smartsocietyproject.pf.TaskRunner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class DemoTaskRunner implements TaskRunner {
    
    private final DemoTaskRequest request;
    private final SmartSocietyApplicationContext ctx;
    
    public DemoTaskRunner(DemoTaskRequest request, 
            SmartSocietyApplicationContext ctx) {
        this.request = request;
        this.ctx = ctx;
    }

    //todo-sv: what is this for
    public JsonNode getStateDescription() {
        return null;
    }

    public void run() {
        try {
            Collective nearbyPeers = ApplicationBasedCollective
                    .createFromQuery(ctx,
                            PeerQuery.create()
                                    .withRule(QueryRule.create("restaurantQA")
                                            .withValue(AttributeType.from("true"))
                                            .withOperation(QueryOperation.equals)
                                    )
                    );
            
            TaskFlowDefinition tfd = TaskFlowDefinition
                    .onDemandWithOpenCall(new RQAProvisioningHandler(), 
                            new RQACompositionHandler(), 
                            new RQANegotiationHandler(),
                            new RQAExecutionHandler())
                    .withCollectiveForProvisioning(nearbyPeers);
            
            CollectiveBasedTask cbt = ctx.registerBuilderForCBTType("rqa", 
                    CBTBuilder.from(tfd)
                    .withTaskRequest(request)).build();
            
            cbt.start();
            
            TaskResult res = cbt.get(3, TimeUnit.MINUTES);
            
            //todo-sv: send result to submiting user
            //todo-sv: there was a nullpointer here--> check execution handler
            //do some easier quality check for the beginning
            System.out.println(res.getResult());
        } catch (PeerManagerException ex) {
            Logger.getLogger(DemoTaskRunner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(DemoTaskRunner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(DemoTaskRunner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeoutException ex) {
            Logger.getLogger(DemoTaskRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
