# Bank

BankService is the banking microservice of the ACMEat project. It handles customer payments through SOAP-based operations and interacts with a PostgreSQL database to register and track payment transactions.

## SOAP Endpoints

| Operation      | Description                                     |
|----------------|-------------------------------------------------|
| `login`        | Authenticates a user and starts a session       |
| `pay`          | Deducts an amount from the user's balance and creates a payment |
| `verifyToken`  | ACME validates the payment token                |
| `confirm`      | ACME confirms the delivery and credits the payment |
| `refund`       | ACME cancels the payment and refunds the user   |
| `logout`       | Terminates the user's session                  |

All endpoints are available via SOAP on port `8000`.

> WSDL file: [`src/BankService.wsdl`](src/BankService.wsdl)

## Database Tables

### `accounts`
- **Description**: Stores user account information.
- **Columns**:
  - `id` (INTEGER, PK): Unique identifier for the account.
  - `username` (TEXT, NOT NULL): User's login name.
  - `password` (TEXT, NOT NULL): User's hashed password.
  - `balance` (NUMERIC(12, 2), NOT NULL): User's account balance (>= 0).

### `payments`
- **Description**: Tracks payment transactions.
- **Columns**:
  - `id` (INTEGER, PK): Unique identifier for the payment.
  - `payer_id` (INTEGER, FK): ID of the user making the payment.
  - `order_id` (INTEGER, UNIQUE, NOT NULL): Associated order ID.
  - `token` (TEXT, UNIQUE, NOT NULL): Unique payment token.
  - `amount` (NUMERIC(12, 2), NOT NULL): Payment amount (> 0).
  - `status` (ENUM, NOT NULL): Payment status (`created`, `validated`, `completed`, `refunded`).

## Running the Service

### Docker Compose

The service is designed to run as part of the global `Bank` setup:

```bash
docker compose up --build
```

> The service will be available at: `http://localhost:8000/?wsdl`
