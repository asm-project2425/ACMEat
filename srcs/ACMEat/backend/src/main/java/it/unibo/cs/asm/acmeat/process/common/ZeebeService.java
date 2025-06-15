package it.unibo.cs.asm.acmeat.process.common;

import java.util.Map;

public interface ZeebeService {
    /**
     * Sends a message to the Zeebe workflow engine.
     *
     * @param messageName The name of the message to send.
     * @param correlationKey The correlation key to identify the message.
     * @param variables The variables to pass along with the message.
     */
    void sendMessage(String messageName, String correlationKey, Map<String, Object> variables);

    /**
     * Completes a job in the Zeebe workflow engine.
     *
     * @param jobType The type of the job to complete.
     * @param variableName The name of the variable to match against the job's variables.
     * @param expectedValue The expected value of the variable to match.
     * @param variables The variables to pass when completing the job.
     */
    void completeJob(String jobType, String variableName, Object expectedValue, Map<String, Object> variables);
}
