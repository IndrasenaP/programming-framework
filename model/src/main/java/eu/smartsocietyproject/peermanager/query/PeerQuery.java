package eu.smartsocietyproject.peermanager.query;

import eu.smartsocietyproject.peermanager.query.Query;

public class PeerQuery extends Query {
    public static PeerQuery create() {
        return new PeerQuery();
    }

    @Override
    public PeerQuery withRule(QueryRule rule) {
        super.withRule(rule);
        return this;
    }
    
    
}
