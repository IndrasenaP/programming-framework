package eu.smartsocietyproject.DTO;

import eu.smartsocietyproject.pf.TaskResult;

public class QualityAssuranceHandlerDTO {
    private TaskResult taskResult;

    public QualityAssuranceHandlerDTO(TaskResult taskResult) {
        this.taskResult = taskResult;
    }

    public TaskResult getTaskResult() {
        return taskResult;
    }
}
