package eu.smartsocietyproject.pf;

import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.ExecutionHandler;
import eu.smartsocietyproject.scenario4.S4TaskRequest;
import org.gitlab.api.GitlabAPI;

import java.io.IOException;
import java.time.Instant;

public class GitlabWaitForCommitExecutionHandler implements ExecutionHandler {

    private final GitlabAPI api;
    private final int projectId;
    private final S4TaskRequest request;
    private final String tag;
    private TaskResult result = WaitingForCommit;

    public GitlabWaitForCommitExecutionHandler(GitlabAPI api, int projectId, S4TaskRequest request, String tag) {
        this.api = api;
        this.projectId = projectId;
        this.request = request;
        this.tag = tag;
    }

    @Override
    public TaskResult execute(ApplicationContext context, CollectiveWithPlan agreed) throws CBTLifecycleException {
        while (!isExpectedCommitBeenPushed()) {
            if (Instant.now().toEpochMilli() > request.getDeadline() ) {
                result = DeadLineExpired;
                return result;
            }
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
            }
        }
        result = CodeCompleted;
        return result;
    }

    private boolean isExpectedCommitBeenPushed() {
        try {
            return api.getCommits(projectId, null, null).stream().anyMatch(c->
                c.getMessage().contains(tag)
            );
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public double resultQoR() {
        return result.QoR();
    }

    @Override
    public TaskResult getResultIfQoRGoodEnough() {
        return result.isQoRGoodEnough() ? result : null;
    }

    static private final TaskResult WaitingForCommit = new TaskResult() {
        @Override
        public String getResult() {
            return "Waiting for commit";
        }

        @Override
        public double QoR() {
            return 0;
        }

        @Override
        public boolean isQoRGoodEnough() {
            return false;
        }
    };

    static private final TaskResult DeadLineExpired = new TaskResult() {
        @Override
        public String getResult() {
            return "Deadline expired";
        }

        @Override
        public double QoR() {
            return 0;
        }

        @Override
        public boolean isQoRGoodEnough() {
            return false;
        }
    };

    static private final TaskResult CodeCompleted = new TaskResult() {
        @Override
        public String getResult() {
            return "Received commit with expected tag in comment";
        }

        @Override
        public double QoR() {
            return 1;
        }

        @Override
        public boolean isQoRGoodEnough() {
            return true;
        }
    };

}
