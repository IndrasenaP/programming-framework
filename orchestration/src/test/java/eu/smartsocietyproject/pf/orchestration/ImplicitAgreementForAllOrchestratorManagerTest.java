package eu.smartsocietyproject.pf.orchestration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.peermanager.helper.CollectiveIntermediary;
import eu.smartsocietyproject.peermanager.query.CollectiveQuery;
import eu.smartsocietyproject.peermanager.query.PeerQuery;
import eu.smartsocietyproject.pf.*;
import org.assertj.core.api.Condition;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

public class ImplicitAgreementForAllOrchestratorManagerTest {
    String basicKindId = "basic";
    CollectiveKind basicKind = CollectiveKind.builder(basicKindId).build();
    CollectiveKindRegistry kindRegistry = CollectiveKindRegistry.builder().register(basicKind).build();

    PeerManager peerManager = new PeerManager() {
        @Override
        public void persistCollective(CollectiveIntermediary collective) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public CollectiveIntermediary readCollectiveById(String id) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public List<CollectiveIntermediary> readCollectiveByQuery(CollectiveQuery query) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public CollectiveIntermediary readCollectiveByQuery(PeerQuery query) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    SmartSocietyApplicationContext context = new SmartSocietyApplicationContext(kindRegistry, peerManager);

    @Test
    public void testCompose() throws Exception {
        ImplicitAgreementForAllOM target = new ImplicitAgreementForAllOM();

        ApplicationBasedCollective provisionedCollective = ApplicationBasedCollective.empty(context, "id", basicKindId);
        List<CollectiveWithPlan> result = target.compose(context, provisionedCollective, createFakeRequest());
        assertThat(result).hasSize(1);
        assertThat(result).are(withCollective(provisionedCollective));

    }

    @Test
    public void testNegotiate() throws Exception {
        ImplicitAgreementForAllOM target = new ImplicitAgreementForAllOM();
        List<CollectiveWithPlan> compositionResult = ImmutableList.of(
            CollectiveWithPlan.of(ApplicationBasedCollective.empty(context, "id", basicKindId), new Plan()),
            CollectiveWithPlan.of(ApplicationBasedCollective.empty(context, "id2", basicKindId), new Plan())
        );
        CollectiveWithPlan result = target.negotiate(context, compositionResult);
        assertThat(result).isEqualTo(compositionResult.get(0));
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testContinuousOrchestration() throws Exception {
        ImplicitAgreementForAllOM target = new ImplicitAgreementForAllOM();
        target.continuousOrchestration(context, createFakeRequest());
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testWithdraw() throws Exception {
        ImplicitAgreementForAllOM target = new ImplicitAgreementForAllOM();
        target.withdraw(new CollectiveBasedTask());
    }

    Condition<CollectiveWithPlan> withCollective(final Collective expected) {
        return new Condition<CollectiveWithPlan>() {
            @Override
            public boolean matches(CollectiveWithPlan value) {

                return expected.equals(value.getCollective());
            }
        };
    }

    private TaskRequest createFakeRequest() {
        return
            new TaskRequest(
                new TaskDefinition(new ObjectMapper().createObjectNode()), "") {
                @Override
                public String getRequest() {
                    return "FAKE";
                }
            };
    }

}