package it.unibo.cs.asm.acmeat.process.common;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import it.unibo.cs.asm.acmeat.exception.JobCompletionException;
import it.unibo.cs.asm.acmeat.exception.RestaurantUpdateNotAllowedException;
import it.unibo.cs.asm.acmeat.exception.ZeebeMessageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static it.unibo.cs.asm.acmeat.process.common.ProcessConstants.*;

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
                .thenAccept(msg -> log.info("Message '{}' sent with correlationKey '{}'",
                        messageName, correlationKey))
                .exceptionally(throwable -> {
                    log.error("Failed to send message '{}': {}", messageName, throwable.getMessage());
                    throw new ZeebeMessageException("Could not publish message: " + messageName, throwable);
                });

    }

    @Override
    public void completeJob(String jobType, String variableName, Object expectedValue, Map<String, Object> variables) {
        List<ActivatedJob> jobs = zeebeClient.newActivateJobsCommand()
                .jobType(jobType)
                .maxJobsToActivate(10)
                .fetchVariables(List.of(variableName))
                .send()
                .join()
                .getJobs();
        log.info("Found {} jobs of type '{}'", jobs.size(), jobType);

        Optional<ActivatedJob> matchingJob = findJobByVariable(jobs, variableName, expectedValue);
        if (matchingJob.isPresent()) {
            ActivatedJob job = matchingJob.get();
            zeebeClient.newCompleteCommand(job.getKey()).variables(variables).send().join();
            log.info("Completed job {} for {}: '{}'", job.getKey(), variableName, expectedValue);
            return;
        }

        log.warn("No matching job found for {}: '{}'", variableName, expectedValue);
        if (JOB_RETRIEVE_RESTAURANT_INFORMATION.equals(jobType)) {
            throw new RestaurantUpdateNotAllowedException();
        }
        throw new JobCompletionException(jobType);
    }

    private Optional<ActivatedJob> findJobByVariable(List<ActivatedJob> jobs, String variableName,
                                                     Object expectedValue) {
        return jobs.stream()
                .filter(job -> expectedValue.equals(job.getVariablesAsMap().get(variableName)))
                .findFirst();
    }

}
