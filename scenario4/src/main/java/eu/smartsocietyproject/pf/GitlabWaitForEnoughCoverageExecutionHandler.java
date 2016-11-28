package eu.smartsocietyproject.pf;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.ExecutionHandler;
import eu.smartsocietyproject.scenario4.S4TaskRequest;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabBuild;
import org.gitlab.api.models.GitlabProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GitlabWaitForEnoughCoverageExecutionHandler implements ExecutionHandler {
    private static final Comparator<GitlabBuild> finishedDateComparator =
        Comparator.comparingLong(
            b -> ZonedDateTime.parse(b.getFinishedAt()).toInstant().toEpochMilli()
        );
    private final GitlabAPI api;
    private final int projectId;
    private final S4TaskRequest request;
    private TaskResult result = coverageResult(0);
    private Logger logger= LoggerFactory.getLogger(this.getClass());

    public GitlabWaitForEnoughCoverageExecutionHandler(
        GitlabAPI api,
        int projectId,
        S4TaskRequest request) {
        this.api = api;
        this.projectId = projectId;
        this.request = request;
    }

    @Override
    public TaskResult execute(ApplicationContext context, CollectiveWithPlan agreed) throws CBTLifecycleException {
        while (true) {
            if (Instant.now().toEpochMilli() > request.getDeadline()) {
                break;
            }
            result = coverageResult(getCoverage());
            logger.info(result.getResult());
            if (result.isQoRGoodEnough()) {
                break;
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
        }
        return result;
    }

    private double getCoverage() {
        try {
            List<GitlabBuild> completedBuilds =
                api.getProjectBuilds(projectId).stream()
                   .filter(b -> b.getFinishedAt() != null)
                   .collect(Collectors.toList());

            if (completedBuilds.isEmpty()) {
                return 0;
            }

            GitlabBuild latestCompleteBuild = Ordering.from(finishedDateComparator).max(completedBuilds);

            if (!"success".equals(latestCompleteBuild.getStatus())) {
                return 0;
            }

            return getCoverageValue(latestCompleteBuild);
        } catch (IOException e1) {
            return 0;
        }
    }

    private double getCoverageValue(GitlabBuild build) throws IOException {
        String trace = getTrace(build);
        Pattern regex = Pattern.compile("COVERAGE:\\s*(0\\.?[\\d]*)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Matcher matcher = regex.matcher(trace);
        if (!matcher.find()) {
            return 0;
        }
        try {
            return Double.valueOf(matcher.group(1));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String getTrace(GitlabBuild build) throws IOException {
        String tailUrl =
            String.format("%s/%d/%s/%d/trace", GitlabProject.URL, projectId, GitlabBuild.URL, build.getId());
        byte[] response = api.retrieve().to(tailUrl, byte[].class);
        return new String(response, "UTF-8");
    }


    @Override
    public double resultQoR() {
        return result.QoR();
    }

    @Override
    public TaskResult getResultIfQoRGoodEnough() {
        return result.isQoRGoodEnough() ? result : null;
    }

    private final TaskResult coverageResult(final double coverage) {
        return new CoverageResult(coverage);
    }

    private class CoverageResult extends TaskResult {
        final double buildCoverage;

        public CoverageResult(double buildCoverage) {
            this.buildCoverage = buildCoverage;
        }

        @Override
        public String getResult() {
            return
                String.format("Current coverage: %f (requested %f)", buildCoverage, request.getCoverage());
        }

        @Override
        public double QoR() {
            return buildCoverage;
        }

        @Override
        public boolean isQoRGoodEnough() {
            return buildCoverage > request.getCoverage();
        }

    }


}
