package it.unibo.cs.asm.acmeat.integration;

import it.unibo.cs.asm.soap.bankclient.*;

public interface BankClientService {
    /**
     * Initializes a session with the bank client service.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return the session ID
     * */
    LoginResponse login(String username, String password);

    /**
     * Creates a payment request.
     *
     * @param sessionId the session ID
     * @param amount the amount to be paid
     * @param orderId the order ID associated with the payment
     * @return a response containing payment details
     */
    CreatePaymentResponse createPayment(String sessionId, double amount, int orderId);

    /**
     * Verifies a payment token.
     *
     * @param sessionId the session ID
     * @param token the payment token to verify
     * @return a response indicating whether the verification was successful
     */
    VerifyTokenResponse verifyToken(String sessionId, String token);

    /**
     * Requests a refund for a payment.
     *
     * @param sessionId the session ID
     * @param paymentId the ID of the payment to refund
     * @return a response indicating whether the refund was successful
     */
    RefundResponse refund(String sessionId, int paymentId);

    /**
     * Confirms a payment.
     *
     * @param sessionId the session ID
     * @param paymentId the ID of the payment to confirm
     * @return a response indicating whether the confirmation was successful
     */
    ConfirmResponse confirm(String sessionId, int paymentId);

    /**
     * Logs out of the bank service.
     *
     * @param sessionId the session ID to log out
     */
    void logout(String sessionId);
}
