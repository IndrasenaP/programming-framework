/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.peermanager.helper;

import eu.smartsocietyproject.peermanager.Peer;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class PersistablePeer extends Peer {

    public PersistablePeer(String id) {
        super(id);
    }

    public String getId() {
        return super.getId();
    }
}
