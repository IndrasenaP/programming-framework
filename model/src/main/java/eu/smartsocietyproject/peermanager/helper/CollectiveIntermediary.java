/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.peermanager.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import eu.smartsocietyproject.peermanager.Peer;
import eu.smartsocietyproject.pf.attributes.StringAttribute;
import java.util.Collection;

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
        return StringAttribute
                .createFromJson(this.getAttribute(this.keyId))
                .getValue();
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

    public Collection<Peer> getMembers() {
        return members.getPeers();
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
        return new CollectiveIntermediary(builder.build());
    }
    
    public static CollectiveIntermediary create(EntityHandler handler) {
        return new CollectiveIntermediary(handler);
    }
}
