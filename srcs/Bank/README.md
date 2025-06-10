# ACMEat Bank Service

## SOAP API Documentation

All SOAP operations are available at:  
**http://localhost:8000**  
> WSDL file: [`src/BankService.wsdl`](src/BankService.wsdl)

---

## Operations Summary

| Operation           | Input Type              | Output Type              | Description                                               |
|---------------------|--------------------------|---------------------------|-----------------------------------------------------------|
| `login`             | `loginRequest`           | `loginResponse`           | Authenticates user and returns session ID                |
| `createPayment`     | `createPaymentRequest`   | `createPaymentResponse`   | Initializes a new payment, returns payment ID            |
| `completePayment`   | `completePaymentRequest` | `completePaymentResponse` | Completes a payment and returns a token                  |
| `verifyToken`       | `verifyTokenRequest`     | `successResponse`         | Verifies a token after customer submits it               |
| `refund`            | `refundRequest`          | `successResponse`         | Refunds a payment if allowed                             |
| `confirm`           | `confirmRequest`         | `successResponse`         | Credits the money to ACME upon delivery                  |
| `logout`            | `logoutRequest`          | `void` (OneWay)           | Ends the current session                                 |

---

## Data Types

### loginRequest
```xml
<login>
  <username>string</username>
  <password>string</password>
</login>
```

### loginResponse
```xml
<loginResponse>
  <success>boolean</success>
  <sessionId>string (optional)</sessionId>
</loginResponse>
```

### createPaymentRequest
```xml
<createPayment>
  <sessionId>string</sessionId>
  <amount>double</amount>
  <orderId>int</orderId>
</createPayment>
```

### createPaymentResponse
```xml
<createPaymentResponse>
  <success>boolean</success>
  <paymentId>int (optional)</paymentId>
</createPaymentResponse>
```

### completePaymentRequest
```xml
<completePayment>
  <sessionId>string</sessionId>
  <paymentId>int</paymentId>
</completePayment>
```

### completePaymentResponse
```xml
<completePaymentResponse>
  <success>boolean</success>
  <token>string (optional)</token>
</completePaymentResponse>
```

### verifyTokenRequest
```xml
<verifyToken>
  <sessionId>string</sessionId>
  <token>string</token>
</verifyToken>
```

### refundRequest
```xml
<refund>
  <sessionId>string</sessionId>
  <paymentId>int</paymentId>
</refund>
```

### confirmRequest
```xml
<confirm>
  <sessionId>string</sessionId>
  <paymentId>int</paymentId>
</confirm>
```

### successResponse
```xml
<successResponse>
  <success>boolean</success>
</successResponse>
```

### logoutRequest (OneWay)
```xml
<logout>
  <sessionId>string</sessionId>
</logout>
```