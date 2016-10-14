/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.smartcom;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class SerializableToJsonSerializer extends StdSerializer<Serializable> {

    public SerializableToJsonSerializer() {
        super(Serializable.class);
    }
    
    @Override
    public void serialize(Serializable arg0, JsonGenerator arg1, 
            SerializerProvider arg2) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(arg0);
        oos.close();
        String value = Base64.getEncoder().encodeToString(baos.toByteArray());
        arg1.writeString(value);
    }
    
}
