package it.unibo.cs.asm.acmeat;

import io.camunda.zeebe.spring.client.annotation.Deployment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Deployment(resources = "classpath:acmeat.bpmn")
public class AcmeatApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcmeatApplication.class, args);
    }
}
