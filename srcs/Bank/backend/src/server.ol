// This service handles both CLIENT and ADMIN (ACME) operations on a single input port. Clients can login, make 
// payments, and logout. Admin (ACME) can verify tokens, confirm or refund payments.
// For simplicity, the same session (`sid`) and variable (`idUsr`) are used throughout. However, in admin operations, 
// the action must be performed on the correct client account, so the actual payer ID is retrieved from the database 
// rather than relying on the session.
// This unified approach keeps the logic compact and was chosen for educational clarity. A more robust design could 
// include role separation and multiple input ports.

include "console.iol"
include "interface.iol"
include "string_utils.iol"
include "database.iol"

inputPort Bank {
    Location: "socket://0.0.0.0:8000"
    Protocol: soap {
        .wsdl = "BankService.wsdl";
        .wsdl.port = "Bank";
    }
    Interfaces: BankInterface
}

cset {
    sid: 
        loginResponse.sid
        payRequest.sid
        verifyTokenRequest.sid
        confirmRequest.sid
        refundRequest.sid
        logoutRequest.sid
}

execution { concurrent }

init {
    println@Console("Bank server is running...")();

    // DB connection
    with(connectionInfo){
        .username = "bankuser"
        .password = "bankpassword"
        .host = "bank_db" // localhost (for local testing)
        .database = "bankdb"
        .driver = "postgresql"
    }
    connect@Database(connectionInfo)()
}

main {

    // Login: set sid and user ID
    login(loginRequest)(loginResponse) {
        username = loginRequest.username;
        password = loginRequest.password;

        println@Console("Login request: " + username + " / " + password)()

        query@Database("SELECT id FROM accounts WHERE username = '" + username + "' AND password = '" + password + 
			"'")(queryResponse)

        if (queryResponse.row[0].id == null) {
            println@Console("Invalid credentials")()
            loginResponse.success = false
        } else {
            loginResponse.sid = csets.sid = new
            loginResponse.success = true
            idUsr = queryResponse.row[0].id
            println@Console("User " + username + " logged in with id " + idUsr)()
            keepRunning = true
        }
    }

    while (keepRunning) {

        // Pay: deduct balance and create payment
        [pay(payRequest)(payResponse) {
            bill = payRequest.bill
            orderId = payRequest.orderId
            getRandomUUID@StringUtils()(uuid)

            println@Console("PAY: order " + orderId + ", amount: " + bill + " for user ID " + idUsr)()

            query@Database("SELECT balance FROM accounts WHERE id = " + idUsr)(query)
            balance = query.row[0].balance

            if (bill <= balance) {
                update@Database("UPDATE accounts SET balance = balance - " + bill + " WHERE id = " + idUsr)(res1)

                queryText = "INSERT INTO payments (token, amount, payer_id, status, order_id) VALUES (:token, " + 
					":amount, :payer_id, cast('created' as payment_status), :order_id)"
                update@Database(queryText {
                    .token = uuid,
                    .amount = bill,
                    .payer_id = idUsr,
                    .order_id = orderId
                })(res2)

                payResponse.token = uuid
                payResponse.success = true
                keepRunning = false
                println@Console("Payment successful. Token: " + uuid)()
            } else {
                println@Console("PAY FAILED: insufficient balance (" + balance + ")")()
                payResponse.success = false
            }
        }]

        // Token validation: move payment to 'validated'
        [verifyToken(verifyTokenRequest)(successResponse) {
            orderId = verifyTokenRequest.orderId
            token = verifyTokenRequest.token

            println@Console("VERIFY TOKEN: token = " + token + ", order_id = " + orderId)()

            query@Database("SELECT id FROM payments WHERE token = '" + token + "' AND order_id = " + orderId +
				" AND status = 'created'")(queryResponse)

            if (queryResponse.row[0].id == null) {
                println@Console("TOKEN INVALID or already processed")()
                successResponse.success = false
            } else {
                update@Database("UPDATE payments SET status = 'validated' WHERE token = '" + token + "'")(res)
                println@Console("Token verified successfully")()
                successResponse.success = true
            }
        }]

        // Confirm: credit to ACME and mark payment completed
        [confirm(confirmRequest)(successResponse) {
            orderId = confirmRequest.orderId
            println@Console("CONFIRM: order_id = " + orderId)()

            query@Database("SELECT amount FROM payments WHERE order_id = " + orderId + " AND status = 'validated'")(
				queryResponse)

            if (queryResponse.row[0].amount == null) {
                println@Console("CONFIRM FAILED: no validated transaction found")()
                successResponse.success = false
            } else {
                amount = queryResponse.row[0].amount

                update@Database("UPDATE payments SET status = 'completed' WHERE order_id = " + orderId)(res1)
                update@Database("UPDATE accounts SET balance = balance + " + amount + " WHERE id = " + idUsr)(res2)

                println@Console("CONFIRM SUCCESS: " + amount + " credited to ACME")()
                successResponse.success = true
                keepRunning = false
            }
        }]

        // Refund: return money to payer and mark refunded
        [refund(refundRequest)(successResponse) {
            orderId = refundRequest.orderId
            println@Console("REFUND: order_id = " + orderId)()

            query@Database("SELECT amount, payer_id FROM payments WHERE order_id = " + orderId + 
				" AND status = 'validated'")(queryResponse)

            if (queryResponse.row[0].amount == null) {
                println@Console("REFUND FAILED: no refundable transaction")()
                successResponse.success = false
            } else {
                amount = queryResponse.row[0].amount
				payer = queryResponse.row[0].payer_id

                update@Database("UPDATE payments SET status = 'refunded' WHERE order_id = " + orderId)(res1)
                update@Database("UPDATE accounts SET balance = balance + " + amount + " WHERE id = " + payer)(res2)

                println@Console("REFUND SUCCESS: " + amount + " refunded to user " + payer)()
                successResponse.success = true
                keepRunning = false
            }
        }]

        // Logout: end session
        [logout(logoutRequest)] {
            println@Console("LOGOUT: user_id = " + idUsr + " - session terminated")()
            keepRunning = false
        }
    }
}
