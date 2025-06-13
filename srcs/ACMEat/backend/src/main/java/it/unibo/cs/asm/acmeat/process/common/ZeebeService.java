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
     * @param correlationKey The correlation key to identify the job.
     * @param variables The variables to pass along with the job completion.
     */
    void completeJob(String jobType, String correlationKey, Map<String, Object> variables);
}
