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
import eu.smartsocietyproject.pf.AttributeType;
import eu.smartsocietyproject.pf.BasicAttribute;
import eu.smartsocietyproject.pf.Member;
import static eu.smartsocietyproject.pf.helper.EntityCore.mapper;

/**
 * PeerIntermediary is a DTO class which allows the local PM proxy to 
 * operate.
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class PeerIntermediary implements MemberIntermediary {
    
    private static final String idFieldName = "id";
    private static final String roleFieldName = "role";
    private final BasicAttribute<String> id;
    private final BasicAttribute<String> role;
    private EntityHandler entity;
    
    protected PeerIntermediary(EntityHandler handler) throws PeerManagerException {
        this.entity = handler;
        this.id = this.entity.getAttribute(idFieldName, AttributeType.STRING);
        this.role = this.entity.getAttribute(roleFieldName, AttributeType.STRING);
    }
    
    @Override
    public String getPeerId() {
        return this.id.getValue();
    }
    
    public JsonNode toJson() {
        return this.entity.toJson();
    }
    
    public static PeerIntermediary createFromJson(String json) throws PeerManagerException {
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
}
