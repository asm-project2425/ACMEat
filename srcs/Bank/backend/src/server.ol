/*
For generating the WSDL file, run:
```
jolie2wsdl \
  --namespace jolie.bank.soap.wsdl \
  --portName BankServicePort \
  --portAddr http://localhost:8000 \
  --outputFile BankService.wsdl \
  generateWsdl.ol
```
*/

include "console.iol"
include "interface.iol"
include "string_utils.iol"
include "database.iol"

inputPort Bank {
    Location: "socket://0.0.0.0:8000"
    Protocol: soap {
        .wsdl = "BankService.wsdl";
        .wsdl.port = "BankServicePort";
    }
    Interfaces: BankInterface
}

cset {
    sid: 
        loginResponse.sessionId
        createPaymentRequest.sessionId
        completePaymentRequest.sessionId
        verifyTokenRequest.sessionId
        refundRequest.sessionId
        confirmRequest.sessionId
        logoutRequest.sessionId
}

execution { concurrent }

init {
    println@Console("Bank server is running...")();

    // DB connection
    with(connectionInfo){
        .username = "bankuser"
        .password = "bankpassword"
        .host = "bank_db" // localhost (for local testing: CLASSPATH=lib/postgresql-42.7.5.jar jolie src/server.ol)
        .database = "bankdb"
        .driver = "postgresql"
    }
    connect@Database(connectionInfo)()
}

main {
    // Login: user authentication and sessionId creation
    login(loginRequest)(loginResponse) {
        username = loginRequest.username;
        password = loginRequest.password;

        println@Console("Login request: " + username + " / " + password)();

        // Uso query parametrica per sicurezza
        query@Database(
            "SELECT id FROM accounts WHERE username = :u AND password = :p" {
                .u = username,
                .p = password
            }
        )(queryResponse)

        if (#queryResponse.row == 0) {
            println@Console("Invalid credentials")();
            loginResponse.success = false
        } else {
            loginResponse.sessionId = csets.sid = new;
            idUsr = queryResponse.row[0].id;
            loginResponse.success = true;
            println@Console("User " + username + " logged in with id " + idUsr)();
            keepRunning = true
        }
    }

    while (keepRunning) {

        // Create payment: payment initiation with order ID and amount
        [createPayment(createPaymentRequest)(createPaymentResponse) {
            amount = createPaymentRequest.amount
            orderId = createPaymentRequest.orderId

            println@Console("CREATE PAYMENT: order " + orderId + ", amount: " + amount + ", created by user ID " + idUsr)()

            // Check if payment already exists
            query@Database("SELECT id FROM payments WHERE order_id = :order_id" {
                .order_id = orderId
            })(queryResponse)

            if (#queryResponse.row > 0) {
                println@Console("PAYMENT FAILED: order " + orderId + " already exists")()
                createPaymentResponse.success = false
            } else {
                queryText = "INSERT INTO payments (amount, creator_id, status, order_id)
                            VALUES (:amount, :creator_id, cast('created' as payment_status), :order_id)
                            RETURNING id"
                query@Database(queryText {
                    .amount = amount,
                    .creator_id = idUsr,
                    .order_id = orderId
                })(res)

                paymentId = res.row[0].id

                println@Console("PAYMENT CREATED: payment ID = " + paymentId)()
                createPaymentResponse.success = true
                createPaymentResponse.paymentId = paymentId
            }
        }]

        // Complete payment: generate token and mark as 'created'
        [completePayment(completePaymentRequest)(completePaymentResponse) {
            paymentId = completePaymentRequest.paymentId

            println@Console("COMPLETE PAYMENT: payment ID = " + paymentId + ", user ID = " + idUsr)()

            // Retrieve payment
            query@Database("
                SELECT amount FROM payments
                WHERE id = :payment_id AND status = 'created'" {
                .payment_id = paymentId
            })(queryResponse)

            if (#queryResponse.row == 0) {
                println@Console("COMPLETE FAILED: payment ID non valido o già elaborato")()
                completePaymentResponse.success = false
            } else {
                amount = queryResponse.row[0].amount

                // Check user balance
                query@Database("SELECT balance FROM accounts WHERE id = :id" {
                    .id = idUsr
                })(balResponse)
                balance = balResponse.row[0].balance

                if (balance < amount) {
                    println@Console("COMPLETE FAILED: saldo insufficiente (" + balance + " < " + amount + ")")()
                    completePaymentResponse.success = false
                } else {
                    // Generate UUID token
                    getRandomUUID@StringUtils()(uuid)

                    // Update payment and user balance
                    update@Database("UPDATE accounts SET balance = balance - :amount WHERE id = :id" {
                        .amount = amount,
                        .id = idUsr
                    })(res1)

                    update@Database("
                        UPDATE payments
                        SET payer_id = :payer_id,
                            token = :token,
                            status = cast('paid' as payment_status),
                            paid_at = CURRENT_TIMESTAMP
                        WHERE id = :payment_id" {
                        .payer_id = idUsr,
                        .token = uuid,
                        .payment_id = paymentId
                    })(res2)

                    println@Console("COMPLETE SUCCESS: token = " + uuid + " - pagamento effettuato da user " + idUsr)()
                    completePaymentResponse.success = true
                    completePaymentResponse.token = uuid
                }
            }
        }]

        // Token validation: move payment to 'validated'
        [verifyToken(verifyTokenRequest)(verifyTokenResponse) {
            token = verifyTokenRequest.token

            println@Console("VERIFY TOKEN: token = " + token + " by user " + idUsr)()

            // Check if token is valid and payment is 'paid'
            query@Database("
                SELECT id FROM payments
                WHERE token = :token AND status = 'paid'" {
                .token = token
            })(queryResponse)

            if (#queryResponse.row == 0) {
                println@Console("VERIFY FAILED: invalid or already validated token")()
                verifyTokenResponse.success = false
            } else {
                update@Database("
                    UPDATE payments
                    SET status = cast('validated' as payment_status),
                        validated_at = CURRENT_TIMESTAMP
                    WHERE token = :token" {
                    .token = token
                })(res)

                println@Console("VERIFY SUCCESS: token validated")()
                verifyTokenResponse.success = true
            }
        }]

        // Confirm: credit amount to ACME and mark as 'completed'
        [confirm(confirmRequest)(confirmResponse) {
            paymentId = confirmRequest.paymentId

            println@Console("CONFIRM: payment ID = " + paymentId + " by user " + idUsr)()

            // Search for validated payment
            query@Database("
                SELECT amount, creator_id FROM payments
                WHERE id = :payment_id AND status = 'validated'" {
                .payment_id = paymentId
            })(queryResponse)

            if (#queryResponse.row == 0) {
                println@Console("CONFIRM FAILED: no validated payment found for ID " + paymentId)()
                confirmResponse.success = false
            } else {
                amount = queryResponse.row[0].amount
                acmeId = queryResponse.row[0].creator_id

                // Update payment status and credit
                update@Database("
                    UPDATE payments
                    SET status = cast('completed' as payment_status),
                        confirmed_at = CURRENT_TIMESTAMP
                    WHERE id = :payment_id" {
                    .payment_id = paymentId
                })(res1)

                update@Database("
                    UPDATE accounts
                    SET balance = balance + :amount
                    WHERE id = :acme_id" {
                    .amount = amount,
                    .acme_id = acmeId
                })(res2)

                println@Console("CONFIRM SUCCESS: " + amount + " credited to ACME (user_id = " + acmeId + ")")()
                confirmResponse.success = true
            }
        }]

        // Refund: return money to payer and mark refunded
        [refund(refundRequest)(refundResponse) {
            paymentId = refundRequest.paymentId

            println@Console("REFUND: payment ID = " + paymentId + " by user " + idUsr)()

            // Check payment with status 'paid' or 'validated'
            query@Database("
                SELECT amount, payer_id FROM payments
                WHERE id = :payment_id AND status IN ('paid', 'validated')" {
                .payment_id = paymentId
            })(queryResponse)

            if (#queryResponse.row == 0) {
                println@Console("REFUND FAILED: no refundable transaction for payment " + paymentId)()
                refundResponse.success = false
            } else {
                amount = queryResponse.row[0].amount
                payer = queryResponse.row[0].payer_id

                // Update payment status and refund amount
                update@Database("
                    UPDATE payments
                    SET status = cast('refunded' as payment_status),
                        confirmed_at = CURRENT_TIMESTAMP
                    WHERE id = :payment_id" {
                    .payment_id = paymentId
                })(res1)

                update@Database("
                    UPDATE accounts
                    SET balance = balance + :amount
                    WHERE id = :payer_id" {
                    .amount = amount,
                    .payer_id = payer
                })(res2)

                println@Console("REFUND SUCCESS: " + amount + " refunded to user " + payer)()
                refundResponse.success = true
            }
        }]

        // Logout: end session
        [logout(logoutRequest)] {
            println@Console("LOGOUT: user_id = " + idUsr + " - session terminated")()
            keepRunning = false
        }
    }
}
