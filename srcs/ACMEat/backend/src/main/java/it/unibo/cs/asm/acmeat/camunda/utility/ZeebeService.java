package it.unibo.cs.asm.acmeat.camunda.utility;

import java.util.Map;

public interface ZeebeService {

    void sendMessage(String messageName, String correlationKey, Map<String, Object> variables);

    boolean completeJob(String jobType, String correlationKey, Map<String, Object> variables);
}
