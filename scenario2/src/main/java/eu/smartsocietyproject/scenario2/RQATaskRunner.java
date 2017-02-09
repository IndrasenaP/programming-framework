/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.scenario2;

import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.Message;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.smartsocietyproject.TaskResponse;
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
import eu.smartsocietyproject.pf.adaptationPolicy.AdaptationPolicies;
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
public abstract class RQATaskRunner implements TaskRunner {

    protected final RQATaskRequest request;
    protected final SmartSocietyApplicationContext ctx;

    public RQATaskRunner(RQATaskRequest request,
            SmartSocietyApplicationContext ctx) {
        this.request = request;
        this.ctx = ctx;
    }

    //todo-sv: what is this for
    @Override
    public JsonNode getStateDescription() {
        return null;
    }
    
    protected Collective queryNearbyPeers() throws PeerManagerException {
        return ApplicationBasedCollective
                .createFromQuery(ctx,
                        PeerQuery.create()
                                .withRule(QueryRule.create("restaurantQA")
                                        .withValue(AttributeType.from("true"))
                                        .withOperation(QueryOperation.equals)
                                )
                );
    }
    
    protected TaskFlowDefinition getDefaultTaskFlowDefinition(Collective peers) {
        return TaskFlowDefinition
                .onDemandWithOpenCall(new RQAProvisioningHandler(),
                        new RQACompositionHandler(),
                        new RQANegotiationHandler(),
                        new RQAExecutionHandler())
                .withCollectiveForProvisioning(peers);
    }
    
    protected CollectiveBasedTask createCBT(TaskFlowDefinition tfd) {
        return ctx.registerBuilderForCBTType("rqa", CBTBuilder.from(tfd)
                        .withTaskRequest(request)).build();
    }
    
    protected void sendResponse(TaskResult res) {
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
    }
}
