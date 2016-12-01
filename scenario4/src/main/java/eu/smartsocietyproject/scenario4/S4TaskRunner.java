package eu.smartsocietyproject.scenario4;

import com.fasterxml.jackson.databind.JsonNode;
import eu.smartsocietyproject.TaskResponse;
import eu.smartsocietyproject.peermanager.query.PeerQuery;
import eu.smartsocietyproject.peermanager.query.QueryOperation;
import eu.smartsocietyproject.peermanager.query.QueryRule;
import eu.smartsocietyproject.pf.*;

import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabProject;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public class S4TaskRunner implements TaskRunner {
    static private final String GitlabUrl = "https://gitlab.com/";
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    private final ApplicationContext context;
    private final S4TaskRequest request;


    public S4TaskRunner(ApplicationContext context, S4TaskRequest request) {
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
                ApplicationBasedCollective.createFromQuery(context, q, S4Application.DEVELOPERS_KIND);
            logger.info("Retrieved developers");

            GitlabAPI api = GitlabAPI.connect(GitlabUrl, request.getToken());
            int projectId = createRepository(api, request);

            CollectiveBasedTask progTask =
                context.getCBTBuilder(S4Application.DEVELOPERS_CBT_TYPE)
                       .withProvisioningHandler(new GitLabMembersAddingProvisioningHandler(api, projectId))
                       .withExecutionHandler(new GitlabWaitForCommitExecutionHandler(
                           api,
                           projectId,
                           request,
                           "@CODE_COMPLETE@"))
                       .withTaskRequest(request)
                       .withInputCollective(devUniverse)
                       .build();
            logger.info("Starting development task.");
            progTask.start();
            while (!progTask.isAfter(CollectiveBasedTask.State.NEGOTIATION)) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    /* NOP */
                }
            }

            logger.info("Negotiation complete, waiting for execution complete");

            if ( TaskResponse.FAIL == getCBTResult(progTask, 0.7) ) {
                logger.info("Development completed with failure");
                return TaskResponse.FAIL;
            }

            logger.info("Development completed with success");
            Collective testTeam = getTestTeam(progTask);

            CollectiveBasedTask testTask =
                context.getCBTBuilder(S4Application.TESTERS_CBT_TYPE)
                       .withTaskRequest(request)
                       .withExecutionHandler(new GitlabWaitForEnoughCoverageExecutionHandler(api, projectId, request))
                       .withInputCollective(testTeam)
                       .build();

            logger.info("Starting testing task.");
            testTask.start();

            TaskResponse response = getCBTResult(testTask, 0.7);
            logger.info("Testing test complete, result: "+response);
            return response;
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

    private int createRepository(GitlabAPI api, S4TaskRequest request) throws IOException, GroupNotFoundException  {
        String groupPrefix = request.getGroup().map(s -> s + "/").orElse("");
        String fullProjectName = groupPrefix + request.getProjectName();

        try {
            return api.getProject(fullProjectName).getId();
        } catch (IOException e) {
            logger.info(String.format("Project [%s] needs to be created", fullProjectName));
        }
        Integer namespaceId = getNamespaceIdFromGroup(api, request.getGroup());

        GitlabProject project =
            api.createProject(request.getProjectName(),
                              namespaceId,
                              "[AUTOMATICALLY CREATED BY SMART SOCIETY SCENARIO4 DEMO]: " + request.getDescription(),
                              true,
                              false,
                              true,
                              false,
                              false,
                              false,
                              0,
                              null);
        logger.info(String.format("Created repository [%s] with id %d", request.getProjectName(), project.getId()));
        return project.getId();
    }


    private Integer getNamespaceIdFromGroup(GitlabAPI api, Optional<String> group) throws GroupNotFoundException {
        return group.map(g -> {
            try {
                return api.getGroup(g).getId();
            } catch (IOException e) {
                throw new GroupNotFoundException(g);
            }
        }).orElse(null);
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
