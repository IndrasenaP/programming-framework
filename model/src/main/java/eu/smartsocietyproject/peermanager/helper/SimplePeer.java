/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.peermanager.helper;

import com.google.common.collect.ImmutableMap;
import eu.smartsocietyproject.peermanager.Peer;
import eu.smartsocietyproject.pf.Attribute;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class SimplePeer extends Peer {
    
    private Map<String, Attribute> attributes = new HashMap<>();

    public SimplePeer(String id) {
        super(id);
    }
    
    public void addAttribute(String key, Attribute attribute) {
        this.attributes.put(key, attribute);
    }
    
    public void addAll(Map<String, Attribute> atts) {
        this.attributes.putAll(atts);
    }

    public ImmutableMap<String, Attribute> getAttributes() {
        return ImmutableMap.copyOf(attributes);
    }
}
