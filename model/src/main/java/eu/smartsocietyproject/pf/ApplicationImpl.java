package eu.smartsocietyproject.pf;

import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.config.Config;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationImpl extends Application {
    private final ConcurrentHashMap<String, Integer> sharedState= new ConcurrentHashMap<>();

    @Override
    public String getApplicationId() {
        throw new UnsupportedOperationException("TODO"); // -=TODO=- (tommaso, 29/08/16)
    }

    @Override
    public void init(Config config) {
        throw new UnsupportedOperationException("TODO"); // -=TODO=- (tommaso, 29/08/16)
    }

    @Override
    public Set<CollectiveKind> listCollectiveKinds() {
        throw new UnsupportedOperationException("TODO"); // -=TODO=- (tommaso, 29/08/16)
    }

    @Override
    public TaskRequest createTaskRequest(TaskDefinition definition) {
        return new MyTaskRequest(definition, definition.getJson().asInt());
    }

    @Override
    public TaskRunner createTaskRunner(TaskRequest request) {
        return new TaskRunner() {
            @Override
            public JsonNode getStateDescription() {
                return null;
            }

            @Override
            public void run() {
                sharedState.put(request.getDefinition().getId().toString(), ((MyTaskRequest)request).getValue());
            }
        };
    }

    class MyTaskRequest extends TaskRequest {
        private final int value;

        MyTaskRequest(TaskDefinition definition, int value) {
            super(definition, "MyTask");
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String getRequest() {

            return Integer.toString(value);
        }
    }
}
