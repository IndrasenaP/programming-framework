/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.scenario2.helper;

import at.ac.tuwien.dsg.smartcom.adapter.OutputAdapter;
import at.ac.tuwien.dsg.smartcom.adapter.annotations.Adapter;
import at.ac.tuwien.dsg.smartcom.adapter.exception.AdapterException;
import at.ac.tuwien.dsg.smartcom.model.Message;
import at.ac.tuwien.dsg.smartcom.model.PeerChannelAddress;
import at.ac.tuwien.dsg.smartcom.utils.PropertiesLoader;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
@Adapter(name = "Email", stateful = false)
public class GreenMailOutputAdapter implements OutputAdapter {
    private static final Logger log = LoggerFactory.getLogger(GreenMailOutputAdapter.class);

    @Override
    public void push(Message message, PeerChannelAddress address) throws AdapterException {
        if (address.getContactParameters().size() == 0) {
            log.error("Peer address does not provide the required email address!");
            throw new AdapterException();
        }

        String recipient = (String) address.getContactParameters().get(0);

        try {
            sendMail(recipient, message.getConversationId(), message.getContent());
        } catch (MessagingException e) {
            throw new AdapterException(e);
        }
    }
    
    private static final Session session = getMailSession();
    private static String username;
    private static String password;

    private static Session getMailSession(){
        final Properties props = new Properties();

        username = PropertiesLoader.getProperty("EmailAdapter.properties", "username");
        password = PropertiesLoader.getProperty("EmailAdapter.properties", "password");
        
        props.setProperty("mail.smtp.host", PropertiesLoader.getProperty("EmailAdapter.properties", "hostOutgoing"));
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.port", PropertiesLoader.getProperty("EmailAdapter.properties", "portOutgoing"));
        props.setProperty("mail.smtp.socketFactory.class", "com.icegreen.greenmail.util.DummySSLSocketFactory" );
        props.setProperty("mail.smtp.socketFactory.fallback", "false" );
        props.setProperty("mail.from", username);

        Session session =  Session.getInstance(props, new javax.mail.Authenticator() {
            @Override protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username,password);
            }
        });

        return session;
    }

    public static void sendMail(String recipient, String subject, String message) throws MessagingException {
        javax.mail.Message msg = new MimeMessage(session);

        InternetAddress addressTo = new InternetAddress(recipient);
        msg.setRecipient(javax.mail.Message.RecipientType.TO, addressTo);
        msg.setRecipient(javax.mail.Message.RecipientType.TO, addressTo);

        msg.setSubject(subject);
        msg.setContent(message, "text/plain");
        msg.setFrom(new InternetAddress(username));

        Transport t = session.getTransport("smtp");
        t.connect(username, password);
        t.sendMessage(msg, msg.getAllRecipients());
        t.close();
    }
}