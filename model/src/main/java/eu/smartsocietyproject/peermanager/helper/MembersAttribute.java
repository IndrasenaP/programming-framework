/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.peermanager.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.ImmutableList;
import eu.smartsocietyproject.peermanager.Peer;
import eu.smartsocietyproject.pf.Attribute;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the members array attribute like returned by 
 * the peer manager.
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class MembersAttribute extends EntityCore implements Attribute {

    protected MembersAttribute(String json) {
        super(json);
    }
    
    protected MembersAttribute(JsonNode node) {
        super(node);
    }

//    public void addMember(String peer) {
//        if (this.root.isArray()) {
//            ((ArrayNode) root).add(peer);
//        }
//    }

    public List<Peer> getPeers() {
        List<Peer> peers = new ArrayList<>();
        ImmutableList.Builder<Peer> builder = ImmutableList.builder();
        if (this.root.isArray()) {
            for (JsonNode node : root) {
                builder.add(new Peer(node.asText(), ""));
            }
        }
        return builder.build();
    }

//    @Override
//    public void parseJson(String attributeValue) {
//        super.parseThis(attributeValue);
//    }

    @Override
    public String toJson() {
        return super.toJson();
    }
    
    protected static MembersAttribute createFromJson(JsonNode node) {
        return new MembersAttribute(node);
    }
    
    public static MembersAttribute createFromJson(String json) {
        return new MembersAttribute(json);
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static final class Builder{
        private MembersAttribute attribute;
        
        public Builder() {
            attribute = createFromJson("[]");
        }
        
        public void addMember(String peer) {
            if (attribute.root.isArray()) {
                ((ArrayNode) attribute.root).add(peer);
            }
        }
        
        public MembersAttribute build() {
            MembersAttribute done = attribute;
            this.attribute = createFromJson("[]");
            return done;
        }
    }

}
