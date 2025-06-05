package it.unibo.cs.asm.acmeat.camunda.utility;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class ZeebeServiceImpl implements ZeebeService {
    private final ZeebeClient zeebeClient;

    @Override
    public void sendMessage(String messageName, String correlationKey, Map<String, Object> variables) {
        zeebeClient.newPublishMessageCommand()
                .messageName(messageName)
                .correlationKey(correlationKey)
                .variables(variables)
                .send()
                .join();
    }

    @Override
    public boolean completeJob(String jobType, String correlationKey, Map<String, Object> variables) {
        List<ActivatedJob> jobs = zeebeClient.newActivateJobsCommand()
                .jobType(jobType)
                .maxJobsToActivate(10)
                .fetchVariables(List.of("correlationKey"))
                .send()
                .join()
                .getJobs();
        log.info("Found {} jobs of type '{}'", jobs.size(), jobType);

        for (ActivatedJob job : jobs) {
            Object keyVar = job.getVariablesAsMap().get("correlationKey");
            if (correlationKey.equals(keyVar)) {
                zeebeClient.newCompleteCommand(job.getKey()).variables(variables).send().join();
                log.info("Completed job {} for correlationKey {}", job.getKey(), correlationKey);
                return true;
            }
        }
        log.warn("No matching job found for correlationKey {}", correlationKey);
        return false;
    }
}
