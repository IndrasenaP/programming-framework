/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pf.helper;

import eu.smartsocietyproject.pf.helper.attributes.MongoMembersAttribute;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import eu.smartsocietyproject.peermanager.CollectiveIntermediary;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.Attribute;
import eu.smartsocietyproject.pf.AttributeType;
import eu.smartsocietyproject.pf.BasicAttribute;
import eu.smartsocietyproject.pf.CollectiveKind;
import eu.smartsocietyproject.pf.Member;
import eu.smartsocietyproject.pf.helper.attributes.MembersAttribute;
import eu.smartsocietyproject.pf.helper.factory.AttributeFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This class has only the function to encapsulate the incoming data from the
 * PeerManager and to bring them into a well known format.
 *
 * This way the ResidentCollective itself does not have to care about the real
 * structure of the returned data.
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class JSONCollectiveIntermediary extends EntityHandler implements CollectiveIntermediary {
    private final static ObjectMapper mapper = new ObjectMapper();
    private final static String keyMembers = "members";
    private final static String keyId = "id";
    private final MembersAttribute members;
    private final BasicAttribute<String> id;
    
    protected JSONCollectiveIntermediary(JsonNode node, 
            AttributeFactory factory) throws PeerManagerException {
        super(node);
        
        this.id = super.getAttribute(keyId, AttributeType.STRING);
        
        this.members = factory.getMembersAttribute(this.root.path(keyMembers));
    }

    protected JSONCollectiveIntermediary(String json, 
            AttributeFactory factory) throws PeerManagerException {
        this(EntityCore.parseJson(json), factory);
    }
    
    protected JSONCollectiveIntermediary(EntityHandler handler, 
            MembersAttribute members, BasicAttribute<String> id) {
        super(handler.root);
        this.members = members;
        this.id = id;
    }
    
    @Override
    public String getId() {
        return this.id.getValue();
    }
    
    @Override
    public Collection<Member> getMembers() {
        return members.getMembers();
    }

    //todo-sv shall we move the merging of defaults and exisitng attributes
    //to the ResidentCollective factory function?
    @Override
    public Map<String, Attribute> getAttributes(CollectiveKind kind) {
        if (!this.root.isObject()) {
            /* TODO actually an exception should returned*/
            return ImmutableMap.of();
        }
        ObjectNode obj = (ObjectNode) this.root;
        Map<String, Attribute> defaultValues = kind.getDefaultValues();
        //Map<String, Attribute> readAttributes = readAttributesFromRoot(kind);
        Map<String, Attribute> readAttributes = 
                super.getAttributes(ImmutableList.of(keyId, keyMembers));
        
        Map<String, Attribute> mergedAttributes = Maps.newHashMap(defaultValues);
        mergedAttributes.putAll(readAttributes);
        return ImmutableMap.copyOf(mergedAttributes);
    }
    
//    private Map<String, Attribute> readAttributesFromRoot() {
//        return ImmutableList.copyOf(this.root.fieldNames())
//                .stream()
//                .filter(fieldName -> !specialAttributes.contains(fieldName))
//                .map(fieldName -> getAttribute(fieldName)
//                        .map(v -> createEntry(fieldName, v)))
//                .filter(optEntry -> optEntry.isPresent())
//                .collect(Collectors
//                        .toMap(optEntry -> optEntry.get().getKey(), 
//                               optEntry -> optEntry.get().getValue()));
//    }

    /* TODO We are ignoring attributes invalid for the AttributeType associated with the field in the kind */
//    private Map<String, Attribute> readAttributesFromRoot(CollectiveKind kind) {
//        Map<String, Attribute> defaultValues = kind.getDefaultValues();
//        return
//            ImmutableList.copyOf(this.root.fieldNames())
//                         .stream()
//                         .filter(fieldname ->
//                                     fieldname != keyMembers && fieldname != keyId &&
//                                         defaultValues.get(fieldname) != null)
//                         .map(fieldname -> kind.fromJson(fieldname, root.get(fieldname))
//                                               .map(v -> createEntry(fieldname, v)))
//                         .filter(entryOpt -> entryOpt.isPresent())
//                         .collect(
//                             Collectors.toMap(
//                                 entryOpt -> entryOpt.get().getKey(),
//                                 entryOpt -> entryOpt.get().getValue()));
//    }
//
//    private <V> Map.Entry<String, V> createEntry(final String s, final V a) {
//        return new Map.Entry<String, V>() {
//            @Override
//            public String getKey() {
//                return s;
//            }
//
//            @Override
//            public V getValue() {
//                return a;
//            }
//
//            @Override
//            public V setValue(V value) {
//                throw new UnsupportedOperationException("TODO"); // -=TODO=- (tommaso, 26/08/16)
//            }
//        };
//
//    }
//    public void addMember(String peerId) {
//        this.members.addMember(peerId);
//    }
//    
//    public void addMember(Peer peer) {
//        this.addMember(peer.getId());
//    }
//
//    public void addMember(PeerIntermediary member) {
//        this.addMember(member.getId());
//    }

    
    
//    @Override
//    protected final void parseThis(String json) {
//        super.parseThis(json);
//        MembersAttribute parsedMembers = getAttributeNode(keyMembers, 
//                MembersAttribute.create());
//        if(parsedMembers.root.isMissingNode()) {
//            parsedMembers = members;
//            addAttributeNode(keyMembers, members);
//        }
//        this.members = parsedMembers;
//    }
    
    public static JSONCollectiveIntermediary create(String json, 
            AttributeFactory factory) throws PeerManagerException {
        return new JSONCollectiveIntermediary(json, factory);
    }
    
//    public static JSONCollectiveIntermediary create(MongoMembersAttribute members, 
//            AttributeFactory factory) throws PeerManagerException {
//        EntityHandler.Builder builder = EntityHandler.builder();
//        builder.addAttributeNode(keyMembers, members);
//        builder.addAttributeNode(keyId, EntityHandler.create(TextNode.valueOf("")));
//        return new JSONCollectiveIntermediary(builder.build(), factory);
//    }

//    public static JSONCollectiveIntermediary create(String id, 
//            MongoMembersAttribute members, AttributeFactory factory) throws PeerManagerException {
//        EntityHandler.Builder builder = EntityHandler.builder();
//        builder.addAttributeNode(keyMembers, members);
//        builder.addAttributeNode(keyId, EntityHandler.create(TextNode.valueOf(id)));
//        return new JSONCollectiveIntermediary(builder.build(), factory);
//    }
    
//    public static JSONCollectiveIntermediary create(EntityHandler handler, 
//            AttributeFactory factory) throws PeerManagerException {
//        return new JSONCollectiveIntermediary(handler, factory);
//    }
    
    public static Builder builder(MembersAttribute members) {
        return new Builder(members);
    }
    
    public static final class Builder {
        private EntityHandler.Builder collective;
        private MembersAttribute members;
        
        private Builder(MembersAttribute members) {
            collective = EntityHandler.builder();
            this.members = members;
            collective.addAttribute(keyMembers, this.members);
        }
        
        public void addAttribute(String name, Attribute att) {
            collective.addAttribute(name, att);
        }
        
        public JSONCollectiveIntermediary build(String id) {
            BasicAttribute<String> idAtt = (BasicAttribute<String>)AttributeType.from(id);
            collective.addAttribute(keyId, idAtt);
            return new JSONCollectiveIntermediary(collective.build(), 
                    members, idAtt);
        }
        
        public JSONCollectiveIntermediary build() {
            return build("");
        }
    }
}
