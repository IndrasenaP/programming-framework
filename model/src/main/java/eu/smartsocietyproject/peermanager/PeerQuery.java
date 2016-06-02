package eu.smartsocietyproject.peermanager;

import java.util.ArrayList;
import java.util.List;

public class PeerQuery {
    
    private final List<PeerQueryRule> rules;
    
    public PeerQuery(){
        this.rules = new ArrayList<>();
    }
    
    public PeerQuery withRule(PeerQueryRule rule) {
        this.rules.add(rule);
        return this;
    }
    
    public List<PeerQueryRule> getPeerQueryRules() {
        return this.rules;
    }
    
    public static PeerQuery create() {
        return new PeerQuery();
    }
}
