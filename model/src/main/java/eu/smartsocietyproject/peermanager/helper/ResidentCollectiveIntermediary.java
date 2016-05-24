/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.peermanager.helper;

import eu.smartsocietyproject.peermanager.Peer;
import java.util.ArrayList;
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
public class ResidentCollectiveIntermediary {

    String id;
    Collection<Peer> members = new ArrayList<>();

    //todo-sv: think about the attributes:
    //--> are they allways simple key->values
    //--> or do they also have key->objects/array mapings?
    //Map<String, String> attributes = new HashMap<>();
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addMember(Peer member) {
        this.members.add(member);
    }

    public Collection<Peer> getMembers() {
        return members;
    }
}
