/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pf;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mongodb.client.MongoCollection;
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.helper.PeerIntermediary;
import eu.smartsocietyproject.peermanager.query.CollectiveQuery;
import eu.smartsocietyproject.peermanager.query.PeerQuery;
import eu.smartsocietyproject.peermanager.query.QueryOperation;
import eu.smartsocietyproject.peermanager.query.QueryRule;
import eu.smartsocietyproject.smartcom.SmartComService;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
public class PeerManagerMongoTest {
    private static final String expectedMemberTim = "tim";
    private static final String expectedMemberTom = "tom";
    private static final String expectedMemberTum = "tum";

    private static final Collection<String> validMembersId =
        ImmutableList.of(
            expectedMemberTim,
            expectedMemberTom,
            expectedMemberTum
        );

    private static final String expPeerAge = "age";
    private static final String expPeerComment = "comment";
    private static final String expAttCountry = "country";
    private static final String expAttLanguage = "language";
    private static final String expAttSince = "since";
    private static final String expColl1Key = "coll1";
    private static final String expColl2Key = "coll2";
    private static final String expColl3Key = "coll3";
    private static final String expLangEnglish = "english";
    private static final String expLangGerman = "german";
    private static final String expCountryA = "Austria";
    private static final String expCountryE = "England";
    private static final int expSince5 = 5;
    private static final int expAge27 = 27;
    private static final int expAge29 = 29;
    private static final String expCommentBlub = "blub";
    private static final String expCommentBlab = "blab";

    private static final CollectiveKind collectiveKind =
        CollectiveKind
            .builder("defaultKind")
            .addAttribute(expAttLanguage, AttributeType.from(""))
            .addAttribute(expAttCountry, AttributeType.from(""))
            .addAttribute(expAttSince, AttributeType.from(0))
            .build();

    private static final CollectiveKindRegistry registry =
        CollectiveKindRegistry.builder().register(collectiveKind).build();

    private static final ApplicationContext context =
        new ApplicationContext() {
            @Override
            public UUID getId() {
                throw new UnsupportedOperationException("TODO"); // -=TODO=- (tommaso, 29/08/16)
            }

            @Override
            public PeerManager getPeerManager() {
                throw new UnsupportedOperationException("TODO"); // -=TODO=- (tommaso, 29/08/16)
            }

            @Override
            public CollectiveKindRegistry getKindRegistry() {
                return registry;
            }

            @Override
            public CBTBuilder registerBuilderForCBTType(String type, CBTBuilder builder) {
                throw new UnsupportedOperationException("TODO"); // -=TODO=- (tommaso, 29/08/16)
            }

            @Override
            public CBTBuilder getCBTBuilder(String type) {
                throw new UnsupportedOperationException("TODO"); // -=TODO=- (tommaso, 29/08/16)
            }

        @Override
        public SmartComService getSmartCom() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        };

    MongoRunner runner;

    private PeerManagerMongoProxy pm;

    public PeerManagerMongoTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @Before
    public void setUp() throws IOException, Collective.CollectiveCreationException, PeerManagerException {
        runner = MongoRunner.withPort(6666);
        pm = PeerManagerMongoProxy.factory(runner.getMongoDb()).create(context);
        createPeers(pm);
        createCollectives(pm);
    }

    @After
    public void tearDown() {
        runner.close();
    }

    private void createCollectives(PeerManagerMongoProxy pm) throws Collective.CollectiveCreationException, PeerManagerException {
        pm.persistCollective(
                addAttributesToCollective(
                    this.expColl1Key,
                    this.expLangEnglish,
                    this.expCountryA).toApplicationBasedCollective());

        pm.persistCollective(
                addAttributesToCollective(
                    this.expColl2Key,
                    this.expLangGerman,
                    this.expCountryA).toApplicationBasedCollective());

        pm.persistCollective(
                addAttributesToCollective(
                    this.expColl3Key,
                    this.expLangEnglish,
                    this.expCountryE).toApplicationBasedCollective());
    }

    private Collective addAttributesToCollective(String id,
                                                 String language,
                                                 String country) throws Collective.CollectiveCreationException {
        return
            ApplicationBasedCollective
                .empty(context, id, "defaultKind")
                .withAttributes(
                    ImmutableMap.of(
                        expAttLanguage, AttributeType.from(language),
                        expAttCountry, AttributeType.from(country),
                        expAttSince, AttributeType.from(this.expSince5)))

                .makeMembersVisible()
                .withMembers(
                    ImmutableList.of(
                        Member.of(this.expectedMemberTim, ""),
                        Member.of(this.expectedMemberTom, ""),
                        Member.of(this.expectedMemberTum, "")));
    }


    private void createPeers(PeerManagerMongoProxy pm) throws PeerManagerException {
        pm.persistPeer(getPeer(expectedMemberTim,
                this.expAge27, this.expCommentBlub));
        pm.persistPeer(getPeer(expectedMemberTom,
                this.expAge29, this.expCommentBlub));
        pm.persistPeer(getPeer(expectedMemberTum,
                this.expAge29, this.expCommentBlab));
    }

    private PeerIntermediary getPeer(String userName, int age, String comment) throws PeerManagerException {
        return PeerIntermediary.builder(userName, "defaultRole")
                .addAttribute(this.expPeerAge, AttributeType.from(age))
                .addAttribute(this.expPeerComment, AttributeType.from(comment))
                .build();
    }

    private void assertMembers(Collection<Member> members, Collection<String> expectedPeerIds) {
        Set<String> membersIds =
            members
                .stream()
                .map(m -> m.getPeerId())
                .collect(Collectors.toSet());
        assertPeersId(membersIds);
    }

    private void assertMembers(Collection<Member> members) {
        assertMembers(members, validMembersId);
    }

    private void assertPeersId(Collection<String> memberIds, Collection<String> expectedPeerIds) {
        assertThat(memberIds)
            .isSubsetOf(expectedPeerIds);
    }

    private void assertPeersId(Collection<String> memberIds) {
        assertPeersId(memberIds, validMembersId);
    }

    @Test
    public void testLoadPeers() throws IOException {
        MongoCollection<Document> peersCollection = pm.getMongoDb()
                .getCollection(MongoConstants.peer);

        assertThat(peersCollection.count()).isEqualTo(3);

        Document peer = peersCollection.find().first();
        assertNotNull(peer);
        assertThat(peer.getString(MongoConstants.id))
            .isIn(validMembersId);
    }

    /**
     * This test should not use the parsing logic we create. It tests if we 
     * write correctly to the MongoDB.
     * @throws IOException
     * @throws PeerManagerException 
     */
    @Test
    public void testPersistCollective() throws IOException, PeerManagerException {
        //load actual collective from DB
        MongoCollection<Document> collectivesCollection = pm.getMongoDb()
                .getCollection(MongoConstants.collection);

        assertEquals(3, collectivesCollection.count());

        Document actualCollective = collectivesCollection.find().first();
        assertEquals(this.expColl1Key, actualCollective.get(MongoConstants.id));
        
        List<Document> actualMembers = actualCollective
                .get(MongoConstants.members, ArrayList.class);
        
        actualMembers.stream()
                .map(peer -> 
                        assertThat(peer.getString(MongoConstants.id))
                                .isIn(validMembersId));

        int expectedCount = 1 + 1 + 1 + 3;//mongoId, collId, members, 3 attributes

        assertThat(actualCollective).hasSize(expectedCount);

        assertThat(actualCollective).containsKeys(
            this.expAttCountry,
            this.expAttLanguage,
            this.expAttSince);

        assertThat(actualCollective.get(this.expAttCountry)).isEqualTo(expCountryA);
        assertThat(actualCollective.get(this.expAttLanguage)).isEqualTo(expLangEnglish);
        assertThat(actualCollective.get(this.expAttSince)).isEqualTo(expSince5);
    }

    @Test
    public void testReadCollective() throws IOException, PeerManagerException {
        ResidentCollective collective = pm.readCollectiveById(this.expColl1Key);
        assertMembers(collective.getMembers());
        
        assertThat(collective.getAttribute(this.expAttCountry))
            .contains(AttributeType.from(this.expCountryA));
        assertThat(collective.getAttribute(this.expAttLanguage))
            .contains(AttributeType.from(this.expLangEnglish));
        assertThat(collective.getAttribute(this.expAttSince))
            .contains(AttributeType.from(this.expSince5));
    }

    @Test
    public void testReadCollectiveByPeerQuery() throws IOException, PeerManagerException {
        ApplicationBasedCollective res = pm
                .createCollectiveFromQuery(PeerQuery
                        .create()
                        .withRule(QueryRule
                                .create(this.expPeerAge)
                                .withOperation(QueryOperation.equals)
                                .withValue(AttributeType.from(29))));

        assertNotNull(res);
        Collection<Member> members = res.getMembers();
        assertThat(members).hasSize(2);

        assertMembers(members, ImmutableList.of(expectedMemberTom, expectedMemberTum));
    }

    @Test
    public void testReadCollectiveByCollectiveQuery() throws IOException, PeerManagerException {
        CollectiveQuery query =
            CollectiveQuery
                .create()
                .withRule(QueryRule
                              .create(this.expAttLanguage)
                              .withOperation(QueryOperation.equals)
                              .withValue(AttributeType.from(this.expLangEnglish)));
        List<ResidentCollective> res = pm.findCollectives(query);
        assertThat(res).hasSize(2);

        for (ResidentCollective coll : res) {
            Collection<Member> members = coll.getMembers();
            assertThat(members).hasSize(3);
            assertMembers(members);



            assertThat(coll.getAttribute(this.expAttLanguage))
                .contains(AttributeType.from(this.expLangEnglish));


            Optional<Attribute> actualCountry = coll.getAttribute(this.expAttCountry);
            assertThat(actualCountry).isPresent();
            assertThat(actualCountry.get())
                .isIn(AttributeType.from(expCountryE), AttributeType.from(expCountryA));
        }
    }
}
