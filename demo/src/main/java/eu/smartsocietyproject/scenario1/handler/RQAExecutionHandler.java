/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.scenario1.handler;

import at.ac.tuwien.dsg.smartcom.callback.NotificationCallback;
import at.ac.tuwien.dsg.smartcom.exception.CommunicationException;
import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.Message;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.smartsocietyproject.scenario1.Demo;
import eu.smartsocietyproject.scenario1.helper.RQAPlan;
import eu.smartsocietyproject.scenario1.helper.RQATaskResult;
import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.CollectiveWithPlan;
import eu.smartsocietyproject.pf.TaskResult;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.ExecutionHandler;
import eu.smartsocietyproject.smartcom.SmartComServiceImpl;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class RQAExecutionHandler implements ExecutionHandler, NotificationCallback {

    private String conversationId = null;
    private RQAPlan plan;
    private RQATaskResult result = new RQATaskResult();
    private ObjectMapper mapper = new ObjectMapper();

    public TaskResult execute(ApplicationContext context, CollectiveWithPlan agreed) throws CBTLifecycleException {
        try {
            //todo-sv: remove this cast
            SmartComServiceImpl sc = (SmartComServiceImpl) context.getSmartCom();

            //todo-sv: maybe registration of rest input adapter belongs also here
            Identifier callback = sc.registerNotificationCallback(this);

            if (!(agreed.getPlan() instanceof RQAPlan)) {
                throw new CBTLifecycleException("Wrong plan!");
            }

            plan = (RQAPlan) agreed.getPlan();

            conversationId = plan.getRequest().getId().toString();

            Properties props = new Properties();
            props.load(Demo.class.getClassLoader()
                    .getResourceAsStream("EmailAdapter.properties"));
            sc.addEmailPullAdapter(conversationId, props);
            
            ObjectNode content = JsonNodeFactory.instance.objectNode();
            content.set("question", JsonNodeFactory.instance
                    .textNode(plan.getRequest().getRequest()));

            Message msg = new Message.MessageBuilder()
                    .setType("ask")
                    .setSubtype("question")
                    .setReceiverId(Identifier.collective(agreed.getCollective().getId()))
                    .setSenderId(Identifier.component("RQA"))
                    .setConversationId(conversationId)
                    .setContent(mapper.writerWithDefaultPrettyPrinter()
                            .writeValueAsString(content))
                    .create();

            sc.send(msg);

            try {
                //here we wait for an optimal solution
                //in getResultIfGoodEnough we offer a result thats "ok"
                while (result.QoR()<1) {
                    Thread.sleep(1000);
                }
            } catch (InterruptedException ex) {
                //return current result in case of timeout
            } finally {
                sc.unregisterNotificationCallback(callback);
                //todo-sv: also unregister email pull adapter
            }

            return result;
        } catch (CommunicationException | IOException ex) {
            throw new CBTLifecycleException(ex);
        }
    }
    
    @Override
    public TaskResult getResultIfQoRGoodEnough() {
        if(this.result.isQoRGoodEnough()) {
            return result;
        }
        
        return null;
    }

    @Override
    public double resultQoR() {
        return result.QoR();
    }

    @Override
    public void notify(Message message) {
        if (conversationId == null) {
            return;
        }

        if (!conversationId.equals(message.getConversationId())) {
            return;
        }

        if (message.getSenderId().getId().equals(plan.getGoogle().getPeerId())) {
            result.setGoogleResult(message.getContent());
            return;
        }

        result.setHumanResult(message.getContent());
    }
}
