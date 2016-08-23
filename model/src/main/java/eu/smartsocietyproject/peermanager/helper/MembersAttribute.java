/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.peermanager.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import eu.smartsocietyproject.peermanager.Peer;
import eu.smartsocietyproject.pf.Attribute;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class MembersAttribute extends EntityCore implements Attribute {

    public MembersAttribute() {
        super("[]");
    }

    public void addMember(String peer) {
        if (this.root.isArray()) {
            ((ArrayNode) root).add(peer);
        }
    }

    public List<Peer> getPeers() {
        List<Peer> peers = new ArrayList<>();
        if (this.root.isArray()) {
            for (JsonNode node : root) {
                peers.add(PeerIntermediary.createEmpty(node.asText()));
            }
        }
        return peers;
    }

    @Override
    public void parseJson(String attributeValue) {
        super.parseThis(attributeValue);
    }

    @Override
    public String toJson() {
        return super.toJson();
    }
    
    public static MembersAttribute create() {
        return new MembersAttribute();
    }

}