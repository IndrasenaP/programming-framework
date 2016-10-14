/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.smartcom;

import at.ac.tuwien.dsg.smartcom.model.PeerChannelAddress;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.Serializable;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class ObjectMapperSingelton {
    private static ObjectMapper mapper = null;
    
    private static void init() {
        if(mapper != null) {
            return;
        }
        mapper = new ObjectMapper();
        mapper.addMixIn(PeerChannelAddress.class, PeerChannelAddressMixIn.class);
//        SimpleModule mod = new SimpleModule("CustomSerializable");
//        mod.addSerializer(new SerializableToJsonSerializer(Serializable.class));
//        mod.addDeserializer(Serializable.class,
//                new JsonToSerializableDeserializer(Serializable.class));
//        mapper.registerModule(mod);
    }
    
    public static ObjectMapper getObjectMapper() {
        init();
        return mapper;
    }
}
