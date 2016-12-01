/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.smartcom;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class JsonToSerializableDeserializer extends StdDeserializer<List<Serializable>> {

    public JsonToSerializableDeserializer() {
        super(ObjectMapperSingelton
                .getObjectMapper()
                .constructType(List.class)
                .withContentType(ObjectMapperSingelton
                .getObjectMapper()
                .constructType(Serializable.class)));
    }
    
    @Override
    public List<Serializable> deserialize(JsonParser arg0, DeserializationContext arg1) throws IOException, JsonProcessingException {
        JsonNode node = arg0.readValueAsTree();
        
        if(!node.isArray()) {
            throw new IOException("Expected JSON-Array!");
        }
        
        List<Serializable> arrayList = new ArrayList<>();
        
        ArrayNode array = (ArrayNode)node;
        for(JsonNode n: array) {
            if(!n.isTextual()) {
                throw new IOException("Only strings are expected!");
            }
            
            ObjectInputStream ois = new ObjectInputStream(
                    new ByteArrayInputStream(Base64.getDecoder()
                            .decode(n.textValue())));
            try {
                Serializable res = (Serializable)ois.readObject();
                ois.close();
                arrayList.add(res);
            } catch (ClassNotFoundException ex) {
                throw new IOException(ex);
            }
        }
        return arrayList;
    }
    
}
