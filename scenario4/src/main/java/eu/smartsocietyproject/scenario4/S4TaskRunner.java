package eu.smartsocietyproject.scenario4;

import com.fasterxml.jackson.databind.JsonNode;
import eu.smartsocietyproject.TaskResponse;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.peermanager.query.PeerQuery;
import eu.smartsocietyproject.peermanager.query.Query;
import eu.smartsocietyproject.peermanager.query.QueryOperation;
import eu.smartsocietyproject.peermanager.query.QueryRule;
import eu.smartsocietyproject.pf.*;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabProject;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public class S4TaskRunner implements TaskRunner {
    static private final String GitlabUrl = "https://gitlab.com/";
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


            ApplicationBasedCollective devUniverse = ApplicationBasedCollective.createFromQuery(context, q);

            GitlabAPI api = GitlabAPI.connect(GitlabUrl, request.getToken());
            int projectId = createRepository(api, request);

            CollectiveBasedTask progTask =
                context.getCBTBuilder(S4Application.DEVELOPERS_CBT_TYPE)
                       .withProvisioningHandler(new GitLabRepositoryCreationProvisioningHandler(api, projectId))
                       .withTaskRequest(request)
                       .withInputCollective(devUniverse)
                       .build();
            progTask.start();

            Collective testTeam;
            while (!progTask.isAfter(CollectiveBasedTask.State.NEGOTIATION)) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    /* NOP */
                }
            }

            testTeam = Collective.complement(
                progTask.getCollectiveProvisioned(),
                progTask.getCollectiveAgreed());

            if ( TaskResponse.FAIL == getCBTResult(progTask, 0.7) )
                return TaskResponse.FAIL;

            CollectiveBasedTask testTask =
                context.getCBTBuilder(S4Application.TESTERS_CBT_TYPE)
                          .withTaskRequest(request)
                          .withInputCollective(testTeam)
                          .build();

            while (true) {
                return getCBTResult(testTask, 0.7);
            }
        } catch (PeerManagerException | GroupNotFoundException | IOException e) {
            return TaskResponse.FAIL;
        }
    }

    private int createRepository(GitlabAPI api, S4TaskRequest request) throws IOException, GroupNotFoundException  {
        Integer namespaceId = getNamespaceIdFromGroup(api, request.getGroup());
        GitlabProject project =
            api.createProject(request.getProjectName(),
                              namespaceId,
                              request.getDescription(),
                              true,
                              false,
                              true,
                              false,
                              false,
                              false,
                              0,
                              null);
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
                return (cbt.get().QoR() < satisfactionLevel) ? TaskResponse.FAIL : TaskResponse.OK;
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
