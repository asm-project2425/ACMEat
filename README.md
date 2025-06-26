# ACMEat

## Overview
ACMEat is a distributed microservices architecture designed to manage food delivery orders from restaurants to customers. The system orchestrates the entire process (from order placement to delivery) ensuring a seamless and modular coordination among all services.

> A full technical report with choreography, BPMN models, SOA diagrams, and further details is available here: [REPORT_ASM.md](./docs/REPORT_ASM.md).

## Deployment
The full system can be launched locally using Docker Compose. The `docker-compose.yaml` file, located in the `srcs` directory, defines all the required services, including microservices, databases, and infrastructure components such as the BPMS (Camunda).

To start the system, run the following command from the `srcs` folder:

```bash
docker-compose up --build
```

Once all services are up and running, you can access the main interfaces at:
- **ACMEat frontend** -> [http://localhost:4321](http://localhost:4321)
- **Camunda Operate** -> [http://localhost:8081](http://localhost:8081) (default credentials: `demo/demo`)
- **Bank frontend** -> [http://localhost:8082](http://localhost:8082) (default credentials: `demo/demo` - customer, `acme/acme` - acmeat)
- **Restaurant 1 frontend** -> [http://localhost:9001](http://localhost:9001)
- **Restaurant 2 frontend** -> [http://localhost:9002](http://localhost:9002)
- **Shipping Company 1 frontend** -> [http://localhost:5001](http://localhost:5001)
- **Shipping Company 2 frontend** -> [http://localhost:5005](http://localhost:5005)

## Structure
- `/docs`: Contains the final report and project documentation.
- `/srcs`: Source code for all services and supporting infrastructure.
    - `/ACMEat`: Main orchestrator service implemented in Spring Boot with Camunda; exposes REST APIs for the frontend and external services.
    - `/Bank`: Banking service implemented in Jolie; provides SOAP endpoints for payment processing.
    - `/GIS`: Simple geolocation service used to calculate distances and retrieve coordinates for specific addresses.
    - `/Restaurant`: Microservice representing a restaurant; handles order availability checks and cancellations.
    - `/ShippingCompany`: Group of microservices for managing shipping operations.
        - `/ShippingManagement`: Main service handling order acceptance, confirmation, and delivery updates.
        - `/VehicleAssigner`: Manages vehicle slot allocation for deliveries.
        - `/VehicleTracker`: Tracks vehicle usage, including start and return events.
    - `docker-compose.yaml`: Configuration file used to orchestrate and launch the entire system via Docker Compose.

> Each subdirectory (e.g., ACMEat, Bank, GIS, etc.) contains its own README.md file with full API documentation and usage instructions specific to that service.