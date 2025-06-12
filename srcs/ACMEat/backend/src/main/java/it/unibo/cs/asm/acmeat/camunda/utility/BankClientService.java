package it.unibo.cs.asm.acmeat.camunda.utility;

import it.unibo.cs.asm.soap.bankclient.*;

public interface BankClientService {
    LoginResponse login(String username, String password);

    CreatePaymentResponse createPayment(String sessionId, double amount, int orderId);

    VerifyTokenResponse verifyToken(String sessionId, String token);

    RefundResponse refund(String sessionId, int paymentId);

    ConfirmResponse confirm(String sessionId, int paymentId);

    void logout(String sessionId);
}
