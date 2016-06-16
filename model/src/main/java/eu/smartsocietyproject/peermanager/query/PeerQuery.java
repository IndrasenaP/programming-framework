package eu.smartsocietyproject.peermanager.query;

/**
 * Up to the name it is the same as the CollectiveQuery.
 * It is represented by a separate class to illustrate that one of the PM functions
 * queries peers and the other queries collectives.
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
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
