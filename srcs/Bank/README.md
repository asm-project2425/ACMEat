# Bank

## SOAP API Documentation

All SOAP operations are available at: **http://localhost:8000**

> WSDL file: [`src/BankService.wsdl`](src/BankService.wsdl)  
> Target namespace: `jolie.bank.soap.wsdl.wsdl`  
> Schema namespace: `jolie.bank.soap.wsdl.xsd`

---

## Operations Summary

| Operation           | Input Type              | Output Type               | Description                                               |
|---------------------|--------------------------|----------------------------|-----------------------------------------------------------|
| `login`             | `loginRequest`           | `loginResponse`            | Authenticates user and returns session ID                |
| `createPayment`     | `createPaymentRequest`   | `createPaymentResponse`    | Initializes a new payment, returns payment ID            |
| `completePayment`   | `completePaymentRequest` | `completePaymentResponse`  | Completes a payment and returns a token                  |
| `verifyToken`       | `verifyTokenRequest`     | `verifyTokenResponse`      | Verifies a token after customer submits it               |
| `refund`            | `refundRequest`          | `refundResponse`           | Refunds a payment if allowed                             |
| `confirm`           | `confirmRequest`         | `confirmResponse`          | Credits the money to ACME upon delivery                  |
| `logout`            | `logoutRequest`          | `void` (OneWay)            | Ends the current session                                 |

---

## Data Types

### loginRequest
```xml
<login xmlns="jolie.bank.soap.wsdl.xsd">
  <username>string</username>
  <password>string</password>
</login>
```

### loginResponse
```xml
<loginResponse xmlns="jolie.bank.soap.wsdl.xsd">
  <success>boolean</success>
  <sessionId>string (optional)</sessionId>
</loginResponse>
```

### createPaymentRequest
```xml
<createPayment xmlns="jolie.bank.soap.wsdl.xsd">
  <amount>double</amount>
  <orderId>int</orderId>
  <sessionId>string</sessionId>
</createPayment>
```

### createPaymentResponse
```xml
<createPaymentResponse xmlns="jolie.bank.soap.wsdl.xsd">
  <success>boolean</success>
  <paymentId>int (optional)</paymentId>
</createPaymentResponse>
```

### completePaymentRequest
```xml
<completePayment xmlns="jolie.bank.soap.wsdl.xsd">
  <paymentId>int</paymentId>
  <sessionId>string</sessionId>
</completePayment>
```

### completePaymentResponse
```xml
<completePaymentResponse xmlns="jolie.bank.soap.wsdl.xsd">
  <success>boolean</success>
  <token>string (optional)</token>
</completePaymentResponse>
```

### verifyTokenRequest
```xml
<verifyToken xmlns="jolie.bank.soap.wsdl.xsd">
  <sessionId>string</sessionId>
  <token>string</token>
</verifyToken>
```

### verifyTokenResponse
```xml
<verifyTokenResponse xmlns="jolie.bank.soap.wsdl.xsd">
  <success>boolean</success>
</verifyTokenResponse>
```

### refundRequest
```xml
<refund xmlns="jolie.bank.soap.wsdl.xsd">
  <paymentId>int</paymentId>
  <sessionId>string</sessionId>
</refund>
```

### refundResponse
```xml
<refundResponse xmlns="jolie.bank.soap.wsdl.xsd">
  <success>boolean</success>
</refundResponse>
```

### confirmRequest
```xml
<confirm xmlns="jolie.bank.soap.wsdl.xsd">
  <paymentId>int</paymentId>
  <sessionId>string</sessionId>
</confirm>
```

### confirmResponse
```xml
<confirmResponse xmlns="jolie.bank.soap.wsdl.xsd">
  <success>boolean</success>
</confirmResponse>
```

### logoutRequest (OneWay)
```xml
<logout xmlns="jolie.bank.soap.wsdl.xsd">
  <sessionId>string</sessionId>
</logout>
```