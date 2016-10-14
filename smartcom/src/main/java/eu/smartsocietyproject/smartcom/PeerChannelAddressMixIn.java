/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.smartcom;

import at.ac.tuwien.dsg.smartcom.model.Identifier;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public interface PeerChannelAddressMixIn {
    @JsonSerialize(using = SerializableToJsonSerializer.class)
    @JsonDeserialize(using = JsonToSerializableDeserializer.class)
    List<? extends Serializable> getContactParameters();
    @JsonIgnore
    Identifier getPeerId();
}
