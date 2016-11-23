package eu.smartsocietyproject.pf;

import com.fasterxml.jackson.databind.JsonNode;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.ProvisioningHandler;
import eu.smartsocietyproject.scenario4.S4TaskRequest;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabAccessLevel;
import org.gitlab.api.models.GitlabGroup;
import org.gitlab.api.models.GitlabNamespace;
import org.gitlab.api.models.GitlabProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class GitLabRepositoryCreationProvisioningHandler implements ProvisioningHandler {
    private final GitlabAPI api;
    private final int projectId;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public GitLabRepositoryCreationProvisioningHandler(GitlabAPI api, int projectId) {
        this.api = api;
        this.projectId = projectId;
    }

    @Override
    public ApplicationBasedCollective provision(
        ApplicationContext context,
        TaskRequest t,
        Optional<Collective> inputCollective) throws CBTLifecycleException {
        Collective collective = inputCollective.get();
        ApplicationBasedCollective abc = inputCollective.get().toApplicationBasedCollective();
        for (Member m : collective.makeMembersVisible().getMembers())
            try {
                JsonNode node = context.getPeerManager().retrievePeer(m.getPeerId()).getData().get("gitlab_id");
                if (node == null || node.isNull() || !node.isInt()) {
                    logger.error("Unable to retrieve gitlab user for peer %s", m.getPeerId());
                    continue;
                }
                int gitlabUser = node.asInt();
                api.addProjectMember(projectId, gitlabUser, GitlabAccessLevel.Developer);
            } catch (PeerManagerException e) {
                logger.error("Unable to retrieve peer %s", m.getPeerId(), e);
                throw new CBTLifecycleException(String.format(
                    "Unable to retrieve peer %s",
                    m.getPeerId()));
            } catch (IOException e) {
                logger.error("Unable to setting up the repository", e);
                throw new CBTLifecycleException("Unable to set up the repository");
            }
        return abc;
    }


}
