/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.smartcom;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class JsonToSerializableDeserializer extends StdDeserializer<Serializable> {

    public JsonToSerializableDeserializer() {
        super(Serializable.class);
    }
    
    @Override
    public Serializable deserialize(JsonParser arg0, DeserializationContext arg1) throws IOException, JsonProcessingException {
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(Base64.getDecoder()
                        .decode(arg0.getValueAsString())));
        try {
            Serializable res = (Serializable)ois.readObject();
            ois.close();
            return res;
        } catch (ClassNotFoundException ex) {
            throw new IOException(ex);
        }
    }
    
}
