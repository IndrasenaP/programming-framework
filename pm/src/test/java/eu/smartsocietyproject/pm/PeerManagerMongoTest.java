/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pm;

import com.mongodb.client.MongoCollection;
import eu.smartsocietyproject.peermanager.Peer;
import eu.smartsocietyproject.peermanager.query.PeerQuery;
import eu.smartsocietyproject.peermanager.query.QueryOperation;
import eu.smartsocietyproject.peermanager.query.QueryRule;
import eu.smartsocietyproject.peermanager.helper.PeerIntermediary;
import eu.smartsocietyproject.peermanager.helper.CollectiveIntermediary;
import eu.smartsocietyproject.peermanager.query.CollectiveQuery;
import eu.smartsocietyproject.pf.attributes.IntegerAttribute;
import eu.smartsocietyproject.pf.attributes.StringAttribute;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
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
        
        pm.persistCollective(
                addPeersToCollective(
                        addAttributesToCollective(
                                CollectiveIntermediary.createEmpty(this.expColl1Key), 
                                this.expLangEnglish, 
                                this.expCountryA)));
        
        pm.persistCollective(
                addPeersToCollective(
                        addAttributesToCollective(
                                CollectiveIntermediary.createEmpty(this.expColl2Key), 
                                this.expLangGerman, 
                                this.expCountryA)));
        
        pm.persistCollective(
                addPeersToCollective(
                        addAttributesToCollective(
                                CollectiveIntermediary.createEmpty(this.expColl3Key), 
                                this.expLangEnglish, 
                                this.expCountryE)));
    }
    
    private CollectiveIntermediary addAttributesToCollective(CollectiveIntermediary collective, 
            String language, String country) {
        collective.addAttribute(this.expAttLanguage, StringAttribute.create(language));
        collective.addAttribute(this.expAttCountry, StringAttribute.create(country));
        collective.addAttribute(this.expAttSince, IntegerAttribute.create(this.expSince5));
        return collective;
    }
    
    private CollectiveIntermediary addPeersToCollective(CollectiveIntermediary collective) {
        collective.addMember(PeerIntermediary.createEmpty(this.expectedMemberTim));
        collective.addMember(PeerIntermediary.createEmpty(this.expectedMemberTom));
        collective.addMember(PeerIntermediary.createEmpty(this.expectedMemberTum));
        return collective;
    }

    private void createPeers(PeerManagerMongoProxy pm) {
        pm.persistPeer(getPeer(expectedMemberTim, 
                this.expAge27, this.expCommentBlub));
        pm.persistPeer(getPeer(expectedMemberTom, 
                this.expAge29, this.expCommentBlub));
        pm.persistPeer(getPeer(expectedMemberTum, 
                this.expAge29, this.expCommentBlab));
    }

    private PeerIntermediary getPeer(String userName, int age, String comment) {
        PeerIntermediary peer = PeerIntermediary.createEmpty(userName);
        peer.addAttribute(this.expPeerAge, IntegerAttribute.create(age));
        peer.addAttribute(this.expPeerComment, StringAttribute.create(comment));
        return peer;
    }
    
    private void assertPeerName(String actualMember) {
        assertTrue(this.expectedMemberTim.equals(actualMember)
                    || this.expectedMemberTom.equals(actualMember)
                    || this.expectedMemberTum.equals(actualMember));
    }
    
    @Test
    public void tesLoadPeers() throws IOException {
        MongoCollection<Document> peersCollection = pm.getMongoDb()
                .getCollection(MongoConstants.peer);
        
        assertEquals(3, peersCollection.count());
        Document peer = peersCollection.find().first();
        assertNotNull(peer);
        assertPeerName(peer.getString(MongoConstants.id));
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
                .get(MongoConstants.members, List.class);
        assertEquals(3, actualMembers.size());
        for (String actualMember : actualMembers) {
            assertPeerName(actualMember);
        }

        int expectedCount = 1 + 1 + 1 + 3;//mongoId, collId, members, 3 attributes

        assertEquals(expectedCount, actualCollective.size());
        assertTrue(actualCollective.containsKey(this.expAttCountry));
        assertTrue(actualCollective.containsKey(this.expAttLanguage));
        assertTrue(actualCollective.containsKey(this.expAttSince));
        
        assertEquals(this.expCountryA, actualCollective.get(this.expAttCountry));
        assertEquals(this.expLangEnglish, actualCollective.get(this.expAttLanguage));
        assertEquals(this.expSince5, actualCollective.get(this.expAttSince));
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
        
        StringAttribute actualCountry = collective
                .getAttribute(this.expAttCountry, StringAttribute.create());
        assertEquals(this.expCountryA, actualCountry.getValue());
        
        StringAttribute actualLanguage = collective
                .getAttribute(this.expAttLanguage, StringAttribute.create());
        assertEquals(this.expLangEnglish, actualLanguage.getValue());
        
        IntegerAttribute actualSince = collective
                .getAttribute(this.expAttSince, IntegerAttribute.create());
        assertEquals(this.expSince5, actualSince.getValue().intValue());
    }

    @Test
    public void testReadCollectiveByPeerQuery() throws IOException {
        CollectiveIntermediary res = pm
                .readCollectiveByQuery(PeerQuery
                        .create()
                        .withRule(QueryRule
                                .create(this.expPeerAge)
                                .withOperation(QueryOperation.equals)
                                .withValue(IntegerAttribute.create(29))));

        assertNotNull(res);
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
                                .withValue(StringAttribute.create(this.expLangEnglish))));

        assertEquals(2, res.size());

        for (CollectiveIntermediary coll : res) {
            assertEquals(3, coll.getMembers().size());

            for (Peer peer : coll.getMembers()) {
                assertPeerName(peer.getId());
            }
            
            assertEquals(this.expLangEnglish, 
                    coll.getAttribute(this.expAttLanguage, 
                            StringAttribute.create()).getValue());
            
            String expectedCountry = coll.getAttribute(this.expAttCountry, 
                    StringAttribute.create()).getValue();
            
            assertTrue(this.expCountryA.equals(expectedCountry) 
                    || this.expCountryE.equals(expectedCountry));
        }
    }
}
