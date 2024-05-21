package it.einjojo.jobs.db;

public class StorageException extends RuntimeException {
    private static final String MESSAGE = "Failed to %s";

    public StorageException(String failedAction, Throwable cause) {
        super(MESSAGE.formatted(failedAction), cause);
    }

}
