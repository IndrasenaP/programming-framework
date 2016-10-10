/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pf.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.util.Map;

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
            MembersAttribute members) throws PeerManagerException {
        super(handler.root);
        this.members = members;
        this.id = handler.getAttribute(keyId, AttributeType.STRING);
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
    public Map<String, Attribute> getAttributes(CollectiveKind kind)
            throws PeerManagerException {
        if (!this.root.isObject()) {
            throw new PeerManagerException("Json-Root-Node was not of type object!");
        }
        ObjectNode obj = (ObjectNode) this.root;
        Map<String, Attribute> defaultValues = kind.getDefaultValues();
        Map<String, Attribute> readAttributes
                = super.getAttributes(ImmutableList.of(keyId, keyMembers));

        Map<String, Attribute> mergedAttributes = Maps.newHashMap(defaultValues);
        mergedAttributes.putAll(readAttributes);
        return ImmutableMap.copyOf(mergedAttributes);
    }

    public static JSONCollectiveIntermediary create(String json,
            AttributeFactory factory) throws PeerManagerException {
        return new JSONCollectiveIntermediary(json, factory);
    }

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

        public JSONCollectiveIntermediary build(String id) throws PeerManagerException {
            Attribute idAtt = AttributeType.from(id);
            collective.addAttribute(keyId, idAtt);
            return new JSONCollectiveIntermediary(collective.build(), members);
        }

        public JSONCollectiveIntermediary build() throws PeerManagerException {
            return build("");
        }
    }
}
