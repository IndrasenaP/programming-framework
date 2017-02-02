package eu.smartsocietyproject.scenario3;

import com.fasterxml.jackson.databind.JsonNode;
import eu.smartsocietyproject.TaskResponse;
import eu.smartsocietyproject.peermanager.query.PeerQuery;
import eu.smartsocietyproject.peermanager.query.QueryOperation;
import eu.smartsocietyproject.peermanager.query.QueryRule;
import eu.smartsocietyproject.pf.*;

import org.slf4j.Logger;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public class S3TaskRunner implements TaskRunner {
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    private final ApplicationContext context;
    private final S3TaskRequest request;


    public S3TaskRunner(ApplicationContext context, S3TaskRequest request) {
        this.context = context;
        this.request = request;
    }

    @Override
    public JsonNode getStateDescription() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public TaskResponse call() {
        try {
            PeerQuery q =
                PeerQuery.create().withRule(QueryRule.create("skill").withOperation(QueryOperation.equals)
                                                     .withValue(AttributeType.from(request.getRequiredSkill())));

            ApplicationBasedCollective devUniverse =
                ApplicationBasedCollective.createFromQuery(context, q, S3Application.DEVELOPERS_KIND);

            return null;
        } catch (Throwable e) {
            logger.error(String.format("Execution of request failed: %s", request), e);
            return TaskResponse.FAIL;
        }
    }

    private Collective getTestTeam(CollectiveBasedTask t) {
        Collective provisionedCollective = t.getCollectiveProvisioned();
        Collective agreedCollective = t.getCollectiveAgreed();

        if (provisionedCollective == null) {
            throw new IllegalStateException("Unexpected empty provisioned collective");
        }

        if (agreedCollective == null) {
            throw new IllegalStateException("Unexpected empty agreed collective");
        }

        return Collective.complement(provisionedCollective, agreedCollective);
    }


    private TaskResponse getCBTResult(CollectiveBasedTask cbt, double satisfactionLevel) {
        while (true) {
            try {
                TaskResult tr = cbt.get();
                return (tr == null || tr.QoR() < satisfactionLevel)
                       ? TaskResponse.FAIL
                       : TaskResponse.OK;
            } catch (InterruptedException e) {
                /* NOP */
            } catch (ExecutionException | CancellationException e) {
                return TaskResponse.FAIL;
            }
        }
    }


    private static class GroupNotFoundException extends RuntimeException {
        public GroupNotFoundException(String group) {
            super(String.format("Could not find group [%s].", group));
        }
    }
}
