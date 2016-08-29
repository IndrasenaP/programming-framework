/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.peermanager.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import eu.smartsocietyproject.pf.Member;
import eu.smartsocietyproject.pf.Peer;
import eu.smartsocietyproject.pf.Attribute;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * This class represents the members array attribute like returned by 
 * the peer manager.
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class MembersAttribute extends EntityCore  {
    private static final String idFieldName = "id";
    private static final String roleFieldName = "role";

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



    public List<Member> getMembers() {
        if (!this.root.isArray()) {
            return ImmutableList.of();
        }

        /* TODO This should raise a deserialization error when id or role are not present */
        List<Member> members=
            StreamSupport.stream(root.spliterator(), false)
                .map(node -> new Member(node.get(idFieldName).asText(), node.get(roleFieldName).asText()))
                .collect(Collectors.toList());

        return ImmutableList.copyOf(members);
    }


//    @Override
//    public void parseJson(String attributeValue) {
//        super.parseThis(attributeValue);
//    }

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

        public void addMember(String peer, String role) {
            if (!attribute.root.isArray())
                return;

            ArrayNode arrayNode = ((ArrayNode) attribute.root);
            ObjectNode memberNode =
                mapper.createObjectNode()
                      .put(idFieldName, peer)
                      .put(roleFieldName, role);

            arrayNode.add(memberNode);
        }

        public void addMember(String peer) {
            addMember(peer, "");
        }

        public void addMember(Member member) {
            addMember(member.getPeerId(), member.getRole());
        }
        
        public MembersAttribute build() {
            MembersAttribute done = attribute;
            this.attribute = createFromJson("[]");
            return done;
        }
    }

}
