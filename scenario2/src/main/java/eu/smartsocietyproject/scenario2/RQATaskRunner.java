/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.scenario2;

import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.Message;
import com.fasterxml.jackson.databind.JsonNode;
import eu.smartsocietyproject.TaskResponse;
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
import eu.smartsocietyproject.scenario2.handler.RQACompositionHandler;
import eu.smartsocietyproject.scenario2.handler.RQAExecutionHandler;
import eu.smartsocietyproject.scenario2.handler.RQANegotiationHandler;
import eu.smartsocietyproject.scenario2.handler.RQAProvisioningHandler;
import eu.smartsocietyproject.smartcom.SmartComServiceImpl;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class RQATaskRunner implements TaskRunner {

    private final RQATaskRequest request;
    private final SmartSocietyApplicationContext ctx;

    public RQATaskRunner(RQATaskRequest request,
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

        TaskResult res = cbt.get(3, TimeUnit.MINUTES);

        if(!res.isQoRGoodEnough()) {
            return TaskResponse.FAIL;
        }
       
        Message respMsg = new Message.MessageBuilder()
                .setType("rqa")
                .setSubtype("answer")
                .setContent(res.getResult())
                .setSenderId(Identifier.component("RQA"))
                .setConversationId("RQA")
                .setReceiverId(this.request.getDefinition().getSender())
                .create();
        
        try {
        //todo-sv: fix interface and remove cast
        ((SmartComServiceImpl) ctx.getSmartCom()).send(respMsg);
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        return TaskResponse.OK;
    }

}
