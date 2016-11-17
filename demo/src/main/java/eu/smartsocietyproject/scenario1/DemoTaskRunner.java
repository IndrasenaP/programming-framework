/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.scenario1;

import com.fasterxml.jackson.databind.JsonNode;
import eu.smartsocietyproject.TaskResponse;
import eu.smartsocietyproject.scenario1.handler.RQACompositionHandler;
import eu.smartsocietyproject.scenario1.handler.RQAExecutionHandler;
import eu.smartsocietyproject.scenario1.handler.RQANegotiationHandler;
import eu.smartsocietyproject.scenario1.handler.RQAProvisioningHandler;
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

    @Override
    public TaskResponse call() throws Exception {
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

        TaskResult res = cbt.get(1, TimeUnit.MINUTES);

        //todo-sv: send result to submiting user
        //todo-sv: there was a nullpointer here--> check execution handler
        //do some easier quality check for the beginning
        System.out.println(res.getResult());
        return null;
    }

}
