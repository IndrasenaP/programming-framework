package eu.smartsocietyproject.scenario3;

import com.fasterxml.jackson.databind.JsonNode;
import eu.smartsocietyproject.TaskResponse;
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
            CollectiveBasedTask testTask =
                context.getCBTBuilder(S3Application.RIDESHARING_COLLECTIVE_KIND)
                       .withTaskRequest(request)
                       .withExecutionHandler(new SmartComPlanCommunicationExecutionHandler())
                       .build();

            logger.info("Starting testing task.");
            testTask.start();

            TaskResponse response = getCBTResult(testTask);
            logger.info("Testing test complete, result: " + response);
            return response;
        } catch (Throwable e) {
            logger.error(String.format("Execution of request failed: %s", request), e);
            return TaskResponse.FAIL;
        }
    }

    private TaskResponse getCBTResult(CollectiveBasedTask cbt) {
        while (true) {
            try {
                TaskResult tr = cbt.get();
                return tr == null ? TaskResponse.FAIL : TaskResponse.OK;
            } catch (InterruptedException e) {
                /* NOP */
            } catch (ExecutionException | CancellationException e) {
                return TaskResponse.FAIL;
            }
        }
    }

}
