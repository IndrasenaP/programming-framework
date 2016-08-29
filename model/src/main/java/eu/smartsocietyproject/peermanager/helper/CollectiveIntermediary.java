/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.peermanager.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import eu.smartsocietyproject.pf.Attribute;
import eu.smartsocietyproject.pf.CollectiveKind;
import eu.smartsocietyproject.pf.Member;

import java.util.Collection;
import java.util.Map;
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
public class CollectiveIntermediary extends EntityHandler {
    private final static ObjectMapper mapper = new ObjectMapper();
    private final static String keyMembers = "members";
    private final static String keyId = "id";
    private final MembersAttribute members;

    protected CollectiveIntermediary(String json) {
        super(json);
        this.members = MembersAttribute
                .createFromJson(this.root.path(keyMembers));
    }
    
    protected CollectiveIntermediary(EntityHandler handler) {
        super(handler.root);
        this.members = MembersAttribute
                .createFromJson(this.root.path(keyMembers));
    }
    
    public String getId() {
        return this.getAttribute(this.keyId);
    }

    public Map<String, Attribute> getAttributes(CollectiveKind kind) {
        if (!this.root.isObject()) {
            /* TODO actually an exception should returned*/
            return ImmutableMap.of();
        }
        ObjectNode obj = (ObjectNode) this.root;
        Map<String, Attribute> defaultValues = kind.getDefaultValues();
        Map<String, Attribute> readAttributes = readAttributesFromRoot(kind);

        Map<String, Attribute> mergedAttributes = Maps.newHashMap(defaultValues);
        mergedAttributes.putAll(readAttributes);
        return ImmutableMap.copyOf(mergedAttributes);
    }

    /* TODO We are ignoring attributes invalid for the AttributeType associated with the field in the kind */
    private Map<String, Attribute> readAttributesFromRoot(CollectiveKind kind) {
        Map<String, Attribute> defaultValues = kind.getDefaultValues();
        return
            ImmutableList.copyOf(this.root.fieldNames())
                         .stream()
                         .filter(fieldname ->
                                     fieldname != keyMembers && fieldname != keyId &&
                                         defaultValues.get(fieldname) != null)
                         .map(fieldname -> kind.fromJson(fieldname, root.get(fieldname))
                                               .map(v -> createEntry(fieldname, v)))
                         .filter(entryOpt -> entryOpt.isPresent())
                         .collect(
                             Collectors.toMap(
                                 entryOpt -> entryOpt.get().getKey(),
                                 entryOpt -> entryOpt.get().getValue()));
    }

    private <V> Map.Entry<String, V> createEntry(final String s, final V a) {
        return new Map.Entry<String, V>() {
            @Override
            public String getKey() {
                return s;
            }

            @Override
            public V getValue() {
                return a;
            }

            @Override
            public V setValue(V value) {
                throw new UnsupportedOperationException("TODO"); // -=TODO=- (tommaso, 26/08/16)
            }
        };

    }
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

    public Collection<Member> getMembers() {
        return members.getMembers();
    }
    
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
    
    public static CollectiveIntermediary create(String json) {
        return new CollectiveIntermediary(json);
    }
    
    public static CollectiveIntermediary create(MembersAttribute members) {
        EntityHandler.Builder builder = EntityHandler.builder();
        builder.addAttributeNode(keyMembers, members);
        builder.addAttributeNode(keyId, EntityHandler.create(TextNode.valueOf("")));
        return new CollectiveIntermediary(builder.build());
    }

    public static CollectiveIntermediary create(String id, MembersAttribute members) {
        EntityHandler.Builder builder = EntityHandler.builder();
        builder.addAttributeNode(keyMembers, members);
        builder.addAttributeNode(keyId, EntityHandler.create(TextNode.valueOf(id)));
        return new CollectiveIntermediary(builder.build());
    }
    
    public static CollectiveIntermediary create(EntityHandler handler) {
        return new CollectiveIntermediary(handler);
    }
}
