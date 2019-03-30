/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pf.helper.attributes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.common.collect.ImmutableList;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.Member;
import eu.smartsocietyproject.pf.AttributeType;
import eu.smartsocietyproject.pf.helper.EntityCore;
import eu.smartsocietyproject.pf.helper.EntityHandler;
import eu.smartsocietyproject.pf.helper.PeerIntermediary;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the members array attribute like returned by the peer
 * manager.
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class MongoMembersAttribute extends EntityCore implements MembersAttribute {

    
    private final ImmutableList<Member> members;

    private MongoMembersAttribute(JsonNode node) throws PeerManagerException {
        super(node);
        this.members = readMembers();
    }
    
    private ImmutableList<Member> readMembers() throws PeerManagerException {
        if (!this.root.isArray()) {
            return ImmutableList.of();
        }

        List<Member> members = new ArrayList<>();
        for (JsonNode node : (root)) {
            EntityHandler handler = EntityHandler.create(node);
            PeerIntermediary peer = PeerIntermediary.create(EntityHandler.create(node));
            members.add(Member.of(peer.getPeerId(), peer.getRole(), peer.getAddress()));
        }

        return ImmutableList.copyOf(members);
    }

    public List<Member> getMembers() {
        return members;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public AttributeType getType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static final class Builder {

        private ArrayNode node;

        Builder() {
            node = JsonNodeFactory.instance.arrayNode();
        }

        public void addMember(PeerIntermediary member) {
            node.add(member.toJson());
        }
        
        public void addMember(Member member) {
            try {
                addMember(PeerIntermediary.of(member));
            } catch (PeerManagerException ex) {
                throw new IllegalStateException(ex);
            }
        }

        public MongoMembersAttribute build() {
            try {
                return new MongoMembersAttribute(node);
            } catch (PeerManagerException ex) {
                throw new IllegalStateException(ex);
            }
        }
        
        public MongoMembersAttribute build(JsonNode node) 
                throws PeerManagerException {
            return new MongoMembersAttribute(node);
        }

        public MongoMembersAttribute build(String actualMembersString) 
                throws PeerManagerException {
            return new MongoMembersAttribute(EntityCore.parseJson(actualMembersString));
        }
    }

}
