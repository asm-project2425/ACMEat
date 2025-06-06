package it.unibo.cs.asm.acmeat.exception;

public class JobCompletionException extends RuntimeException {
    public JobCompletionException(String jobType) {
        super("Unable to complete job: " + jobType + ". Process is not in the expected state.");
    }
}

