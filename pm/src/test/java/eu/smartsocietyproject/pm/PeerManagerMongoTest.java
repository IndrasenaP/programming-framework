/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pm;

import eu.smartsocietyproject.pm.helper.TestIntAttribute;
import eu.smartsocietyproject.pm.helper.TestCollective;
import eu.smartsocietyproject.pm.helper.TestStringAttribute;
import com.mongodb.client.MongoCollection;
import eu.smartsocietyproject.peermanager.Peer;
import eu.smartsocietyproject.peermanager.query.PeerQuery;
import eu.smartsocietyproject.peermanager.query.QueryOperation;
import eu.smartsocietyproject.peermanager.query.QueryRule;
import eu.smartsocietyproject.peermanager.helper.SimplePeer;
import eu.smartsocietyproject.peermanager.helper.CollectiveIntermediary;
import eu.smartsocietyproject.peermanager.query.CollectiveQuery;
import eu.smartsocietyproject.pf.Attribute;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class PeerManagerMongoTest {
    private final String expectedMemberTim = "tim";
    private final String expectedMemberTom = "tom";
    private final String expectedMemberTum = "tum";
    
    private final String expPeerAge = "age";
    private final String expPeerComment = "comment";
    private final String expAttCountry = "country";
    private final String expAttLanguage = "language";
    private final String expAttSince = "since";
    private final String expColl1Key = "coll1";
    private final String expColl2Key = "coll2";
    private final String expColl3Key = "coll3";
    private final String expLangEnglish = "english";
    private final String expLangGerman = "german";
    private final String expCountryA = "Austria";
    private final String expCountryE = "England";
    private final int expSince5 = 5;
    private final int expAge27 = 27;
    private final int expAge29 = 29;
    private final String expCommentBlub = "blub";
    private final String expCommentBlab = "blab";
    
    private PeerManagerMongoProxy pm;

    public PeerManagerMongoTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @Before
    public void setUp() throws IOException {
        pm = new PeerManagerMongoProxy(6666);
        createPeers(pm);
        createCollectives(pm);
    }

    @After
    public void tearDown() {
        pm.close();
    }

    private void createCollectives(PeerManagerMongoProxy pm) {
        pm.persistCollective(new TestCollective(this.expColl1Key,
                this.getPeersForCollective(),
                this.getAttsForCollective(this.expLangEnglish, 
                        this.expCountryA)));
        pm.persistCollective(new TestCollective(this.expColl2Key,
                this.getPeersForCollective(),
                this.getAttsForCollective(this.expLangGerman, 
                        this.expCountryA)));
        pm.persistCollective(new TestCollective(this.expColl3Key,
                this.getPeersForCollective(),
                this.getAttsForCollective(this.expLangEnglish, 
                        this.expCountryE)));
    }

    private Map<String, Attribute> getAttsForCollective(String language,
            String country) {
        Map<String, Attribute> atts = new HashMap<>();
        atts.put(this.expAttLanguage, new TestStringAttribute(language));
        atts.put(this.expAttCountry, new TestStringAttribute(country));
        atts.put(this.expAttSince, new TestIntAttribute(this.expSince5));
        return atts;
    }

    private List<Peer> getPeersForCollective() {
        List<Peer> members = new ArrayList<>();
        members.add(new SimplePeer(this.expectedMemberTim));
        members.add(new SimplePeer(this.expectedMemberTom));
        members.add(new SimplePeer(this.expectedMemberTum));
        return members;
    }

    private void createPeers(PeerManagerMongoProxy pm) {
        pm.persistPeer(getPeer(expectedMemberTim, 
                this.expAge27, this.expCommentBlub));
        pm.persistPeer(getPeer(expectedMemberTom, 
                this.expAge29, this.expCommentBlub));
        pm.persistPeer(getPeer(expectedMemberTum, 
                this.expAge29, this.expCommentBlab));
    }

    private SimplePeer getPeer(String userName, int age, String comment) {
        SimplePeer peer = new SimplePeer(userName);
        peer.addAttribute(this.expPeerAge, new TestIntAttribute(age));
        peer.addAttribute(this.expPeerComment, new TestStringAttribute(comment));
        return peer;
    }
    
    private void assertPeerName(String actualMember) {
        assertTrue(this.expectedMemberTim.equals(actualMember)
                    || this.expectedMemberTom.equals(actualMember)
                    || this.expectedMemberTum.equals(actualMember));
    }

    @Test
    public void testPersistCollective() throws IOException {
        //load actual collective from DB
        MongoCollection<Document> collectivesCollection = pm.getMongoDb()
                .getCollection(MongoConstants.collection);

        assertEquals(3, collectivesCollection.count());

        Document actualCollective = collectivesCollection.find().first();
        assertEquals(this.expColl1Key, actualCollective.get(MongoConstants.id));

        List<String> actualMembers = actualCollective
                .get(MongoConstants.peers, List.class);
        assertEquals(3, actualMembers.size());
        for (String actualMember : actualMembers) {
            assertPeerName(actualMember);
        }

        List<Document> actualAttributes = actualCollective
                .get(MongoConstants.attributes, List.class);
        assertEquals(3, actualAttributes.size());
        
        for(Document doc: actualAttributes) {
            assertTrue(doc.containsKey(MongoConstants.key));
            assertTrue(doc.containsKey(MongoConstants.value));
            assertTrue(doc.containsKey(MongoConstants.type));
            
            if(doc.getString(MongoConstants.key).equals(this.expAttCountry)) {
                assertEquals(this.expCountryA, doc.get(MongoConstants.value));
                assertEquals(TestStringAttribute.class.getName(), 
                        doc.get(MongoConstants.type));
            } else if(doc.getString(MongoConstants.key).equals(this.expAttLanguage)) {
                assertEquals(this.expLangEnglish, doc.get(MongoConstants.value));
                assertEquals(TestStringAttribute.class.getName(), 
                        doc.get(MongoConstants.type));
            } else if(doc.getString(MongoConstants.key).equals(this.expAttSince)) { 
                assertEquals(String.valueOf(this.expSince5), 
                        doc.get(MongoConstants.value));
                assertEquals(TestIntAttribute.class.getName(), 
                        doc.get(MongoConstants.type));
            } else {
                fail();
            }
        }
    }

    @Test
    public void testReadCollective() throws IOException {
        CollectiveIntermediary collective
                = pm.readCollectiveById(this.expColl1Key);
        assertNotNull(collective);

        Collection<Peer> members = collective.getMembers();
        assertEquals(3, members.size());
        for (Peer actualMember : members) {
            assertPeerName(actualMember.getId());
        }

        Map<String, Attribute> actualAttributes = collective.getAttributes();
        assertEquals(3, actualAttributes.size());
        assertTrue(actualAttributes.containsKey(this.expAttCountry));
        assertTrue(actualAttributes.containsKey(this.expAttLanguage));
        assertTrue(actualAttributes.containsKey(this.expAttSince));

        assertTrue(actualAttributes.get(this.expAttSince) instanceof TestIntAttribute);
        assertTrue(actualAttributes.get(this.expAttCountry) instanceof TestStringAttribute);
        assertTrue(actualAttributes.get(this.expAttLanguage) instanceof TestStringAttribute);

        assertEquals(String.valueOf(this.expSince5), actualAttributes
                .get(this.expAttSince).toString());
        assertEquals(this.expCountryA, actualAttributes
                .get(this.expAttCountry).toString());
        assertEquals(this.expLangEnglish, actualAttributes
                .get(this.expAttLanguage).toString());
    }

    @Test
    public void testReadCollectiveByPeerQuery() throws IOException {
        CollectiveIntermediary res = pm
                .readCollectiveByQuery(PeerQuery
                        .create()
                        .withRule(QueryRule
                                .create(this.expPeerAge)
                                .withOperation(QueryOperation.equals)
                                .withValue(new TestIntAttribute(29))));

        assertNotNull(res);
        assertTrue(res.getAttributes().isEmpty());
        assertEquals(2, res.getMembers().size());

        for (Peer peer : res.getMembers()) {
            assertTrue(expectedMemberTom.equals(peer.getId())
                    || expectedMemberTum.equals(peer.getId()));
        }
    }

    @Test
    public void testReadCollectiveByCollectiveQuery() throws IOException {
        List<CollectiveIntermediary> res = pm
                .readCollectiveByQuery(CollectiveQuery
                        .create()
                        .withRule(QueryRule
                                .create(this.expAttLanguage)
                                .withOperation(QueryOperation.equals)
                                .withValue(new TestStringAttribute(this.expLangEnglish))));

        assertEquals(2, res.size());

        for (CollectiveIntermediary coll : res) {
            assertEquals(3, coll.getMembers().size());

            for (Peer peer : coll.getMembers()) {
                assertPeerName(peer.getId());
            }
            
            assertEquals(3, coll.getAttributes().size());

            assertTrue(coll.getAttributes().containsKey(this.expAttLanguage));
            assertEquals(this.expLangEnglish, coll.getAttributes()
                    .get(this.expAttLanguage).toString());

            assertTrue(coll.getAttributes().containsKey(this.expAttCountry));
            assertTrue(this.expCountryA.equals(coll.getAttributes()
                    .get(this.expAttCountry).toString())
                    || this.expCountryE.equals(coll.getAttributes()
                            .get(this.expAttCountry).toString()));
        }
    }
}
