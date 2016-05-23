/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pm;

import eu.smartsocietyproject.peermanager.Peer;
import eu.smartsocietyproject.peermanager.helper.PersistablePeer;
import eu.smartsocietyproject.pf.Attribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public abstract class ConvertHelper {
	public static List<String> convertPeers(Set<Peer> peers) {
		List<String> arrayList = new ArrayList<>();

		for (Peer peer : peers) {
			if (peer instanceof PersistablePeer) {
				arrayList.add(((PersistablePeer) peer).getId());
			}
		}

		return arrayList;
	}
	
	public static Map<String, String> convertAttributes(Map<String, Attribute> attributes) {
		Map<String, String> convertedAtts = new HashMap<>();
		
		for(Map.Entry<String, Attribute> entry: attributes.entrySet()){
			convertedAtts.put(entry.getKey(), entry.getValue().toString());
		}
		
		return convertedAtts;
	}
}
