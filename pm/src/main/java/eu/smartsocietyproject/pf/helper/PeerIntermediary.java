/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pf.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.smartsocietyproject.peermanager.MemberIntermediary;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.Attribute;
import eu.smartsocietyproject.pf.AttributeType;
import eu.smartsocietyproject.pf.BasicAttribute;
import eu.smartsocietyproject.pf.Member;
import static eu.smartsocietyproject.pf.helper.EntityCore.mapper;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PeerIntermediary is a DTO class which allows the local PM proxy to 
 * operate.
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class PeerIntermediary extends EntityHandler implements MemberIntermediary {
    
    private static final String idFieldName = "id";
    private static final String roleFieldName = "role";
    private static final String deliveryAddress = "deliveryAddress";
    private final BasicAttribute<String> id;
    private final BasicAttribute<String> role;
    //private EntityHandler entity;
    
    //todo-sv: constructor throwing exceptions or not?
    protected PeerIntermediary(EntityHandler handler) throws PeerManagerException {
        super(handler.root);
        this.id = this.getAttribute(idFieldName, AttributeType.STRING);
        this.role = this.getAttribute(roleFieldName, AttributeType.STRING);
    }
    
    @Override
    public String getPeerId() {
        return this.id.getValue();
    }
    
    public static PeerIntermediary create(String json) throws PeerManagerException {
        return new PeerIntermediary(EntityHandler.create(json));
    }
    
    public static PeerIntermediary create(EntityHandler attributes) throws PeerManagerException {
        return new PeerIntermediary(attributes);
    }
    
    public static PeerIntermediary of(Member member) throws PeerManagerException {
        ObjectNode memberNode
                    = mapper.createObjectNode()
                    .put(idFieldName, member.getPeerId())
                    .put(roleFieldName, member.getRole());
        return create(EntityHandler.create(memberNode));
    }

    @Override
    public String getRole() {
        return this.role.getValue();
    }
    
    public static Builder builder(String id, String role) {
        return new Builder(id, role);
    }
    
    public static final class Builder extends EntityHandler.Builder {       
        
        public Builder(String id, String role) {
            super();
            super.addAttribute(idFieldName, AttributeType.from(id));
            super.addAttribute(roleFieldName, AttributeType.from(role));
        }
        
        public Builder addDeliveryAddress(Attribute address) {
            return this.addAttribute(deliveryAddress, address);
        }

        @Override
        public Builder addAttributeNode(String name, EntityCore attribute) {
            super.addAttributeNode(name, attribute); //To change body of generated methods, choose Tools | Templates.
            return this;
        }

        @Override
        public Builder addAttribute(String name, Attribute attribute) {
            super.addAttribute(name, attribute); //To change body of generated methods, choose Tools | Templates.
            return this;
        }
        
        @Override
        public PeerIntermediary build() {
            try {
                return PeerIntermediary.create(super.build());
            } catch (PeerManagerException ex) {
                //this can not happen
            }
            return null;
        }
    }
}
