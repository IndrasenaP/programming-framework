/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.scenario2.helper.GreenMail;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import eu.smartsocietyproject.scenario2.Scenario2;
import eu.smartsocietyproject.scenario2.helper.JsonPeer;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class RunMailServer {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final GreenMail greenMail = new GreenMail(ServerSetupTest.ALL);

    /**
     * @param args the command line arguments
     */
    public static void start() throws IOException {
        greenMail.start();

         List<JsonPeer> peers = mapper.readValue(Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("Peers.json"),
                mapper.getTypeFactory()
                        .constructCollectionType(List.class, JsonPeer.class));
        
        peers.stream().filter(peer -> peer.getChannelType().equals("Email"))
                .forEach(peer -> greenMail.setUser(peer.getChannel(), 
                        peer.getName(), peer.getName()));
        
        Properties props = new Properties();
        props.load(Scenario2.class.getClassLoader()
                .getResourceAsStream("EmailAdapter.properties"));
        
        greenMail.setUser(props.getProperty("username"), 
                props.getProperty("username"), props.getProperty("password"));
    }
    
}
