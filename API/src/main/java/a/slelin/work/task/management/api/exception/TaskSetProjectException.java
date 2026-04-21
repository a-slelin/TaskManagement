package a.slelin.work.task.management.api.exception;

import a.slelin.work.task.management.core.exception.BusinessFault;

public class TaskSetProjectException extends BusinessFault {
    public TaskSetProjectException(String message) {
        super(message);
    }
}
