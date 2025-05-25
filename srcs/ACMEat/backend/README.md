# Camunda Workers
This codebase contains the Camunda 8 external workers that execute the `acmeat.bpmn` process found in 
`src/main/resources`.
When the workers are running, each one subscribes to the jobs defined in the diagram and invokes the required REST/SOAP 
endpoints—bringing every BPMN task to life end-to-end.

## Build and Run with Docker

Move to the root directory and run the following commands to build and run the Docker container:
```bash
docker build -t acmeat-workers .
```
```bash
docker run --rm -p 8080:8080 \
  --network camunda-platform \
  -e ZEEBE_GATEWAY_ADDRESS=http://zeebe:26500 \
  acmeat-workers
```

> **Note:** make sure a Camunda 8 broker (Zeebe) **and all required microservices (bank, shipping, customer,
restaurant, …) are already running and reachable** before starting the acmeat-workers container.
