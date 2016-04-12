package eu.smartsocietyproject.pf.orchestration;

import com.google.common.collect.ImmutableList;
import eu.smartsocietyproject.pf.*;
import org.assertj.core.api.Condition;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;

public class ImplicitAgreementForAllOrchestratorManagerTest {
    SmartSocietyApplicationContext context = new SmartSocietyApplicationContext();

    @Test
    public void testCompose() throws Exception {
        ImplicitAgreementForAllOM target = new ImplicitAgreementForAllOM();

        Collective provisionedCollective = new ResidentCollective(context, "id", "basic");
        List<CollectiveWithPlan> result = target.compose(provisionedCollective, new TaskRequest());
        assertThat(result).hasSize(1);
        assertThat(result).are(withCollective(provisionedCollective));
    }

    @Test
    public void testNegotiate() throws Exception {
        ImplicitAgreementForAllOM target = new ImplicitAgreementForAllOM();
        List<CollectiveWithPlan> compositionResult = ImmutableList.of(
            CollectiveWithPlan.of(new ResidentCollective(context, "id", "basic"), new Plan()),
            CollectiveWithPlan.of(new ResidentCollective(context, "id2", "basic"), new Plan())
        );
        CollectiveWithPlan result = target.negotiate(compositionResult);
        assertThat(result).isEqualTo(compositionResult.get(0));
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testContinuousOrchestration() throws Exception {
        ImplicitAgreementForAllOM target = new ImplicitAgreementForAllOM();
        target.continuousOrchestration(new TaskRequest());
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
                return expected.equals(value);
            }
        };
    }

}