/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.peermanager.helper;

import eu.smartsocietyproject.peermanager.Peer;
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

    private String keyMembers = "members";
    private MembersAttribute members;
    
    protected CollectiveIntermediary() {
        super("id");
        this.members = super.addAttributeNode(this.keyMembers, 
                MembersAttribute.create());
    }
    
    public void addMember(String peerId) {
        
    }

    public void addMember(PeerIntermediary member) {
        this.members.addMember(member.getId());
    }

    public Collection<Peer> getMembers() {
        return members.getPeers();
    }
    
    @Override
    protected final void parseThis(String json) {
        super.parseThis(json);
        MembersAttribute parsedMembers = getAttributeNode(keyMembers, 
                MembersAttribute.create());
        if(parsedMembers.root.isMissingNode()) {
            parsedMembers = members;
            addAttributeNode(keyMembers, members);
        }
        this.members = parsedMembers;
    }
    
    public static CollectiveIntermediary createEmpty() {
        return new CollectiveIntermediary();
    }
    
    public static CollectiveIntermediary createEmpty(String id) {
        CollectiveIntermediary coll = createEmpty();
        coll.setId(id);
        return coll;
    }
    
    public static CollectiveIntermediary create(String json) {
        CollectiveIntermediary coll = new CollectiveIntermediary();
        coll.parseThis(json);
        return coll;
    }
}
