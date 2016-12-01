/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.smartcom;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.List;
import javax.sql.rowset.serial.SerialArray;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class SerializableToJsonSerializer extends StdSerializer<List<Serializable>> {

    public SerializableToJsonSerializer() {
        super(ObjectMapperSingelton
                .getObjectMapper()
                .constructType(List.class)
                .withContentType(ObjectMapperSingelton
                .getObjectMapper()
                .constructType(Serializable.class)));
    }
    
    @Override
    public void serialize(List<Serializable> arg0, JsonGenerator arg1, 
            SerializerProvider arg2) throws IOException {
        
        ArrayNode node = ObjectMapperSingelton
                .getObjectMapper()
                .getNodeFactory()
                .arrayNode(arg0.size());
        
        for(Serializable s: arg0) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(s);
            oos.close();
            String value = Base64.getEncoder().encodeToString(baos.toByteArray());
            node.add(value);
        }
        
        arg1.writeTree(node);
    }
    
}
