package it.unibo.cs.asm.acmeat.process.orderManagement.worker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import it.unibo.cs.asm.acmeat.integration.BankClientService;
import it.unibo.cs.asm.acmeat.process.common.ZeebeService;
import it.unibo.cs.asm.soap.bankclient.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static it.unibo.cs.asm.acmeat.process.common.ProcessConstants.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class BankWorkers {
    private final ZeebeService zeebeService;
    private final BankClientService bankService;

    private static final String ACME_USERNAME = "acme";
    private static final String ACME_PASSWORD = "acme";

    @JobWorker(type = JOB_PAYMENT_REQUEST)
    public void paymentRequest(@Variable int orderId, @Variable double orderPrice, @Variable double shippingCost) {
        LoginResponse loginResponse = bankService.login(ACME_USERNAME, ACME_PASSWORD);
        double amount = orderPrice + shippingCost;
        CreatePaymentResponse createPaymentResponse = bankService.createPayment(loginResponse.getSessionId(), amount,
                orderId);
        bankService.logout(loginResponse.getSessionId());

        zeebeService.sendMessage(MSG_PAYMENT_REQUEST, String.valueOf(orderId), Map.of(VAR_PAYMENT_ID,
                createPaymentResponse.getPaymentId()));
    }

    @JobWorker(type = JOB_VERIFY_PAYMENT_TOKEN)
    public void verifyPaymentToken(@Variable String paymentToken, @Variable int orderId) {
        LoginResponse loginResponse = bankService.login(ACME_USERNAME, ACME_PASSWORD);
        VerifyTokenResponse verifyResponse = bankService.verifyToken(loginResponse.getSessionId(), paymentToken);
        bankService.logout(loginResponse.getSessionId());

        if (!verifyResponse.isSuccess()) {
            log.warn("Payment verification failed for token {}", paymentToken);
        } else {
            log.info("Payment verification successful for token {}", paymentToken);
        }

        zeebeService.sendMessage(MSG_PAYMENT_VALIDITY, String.valueOf(orderId), Map.of(VAR_VALID_PAYMENT,
                verifyResponse.isSuccess()));
    }

    @JobWorker(type = JOB_CONFIRM_PAYMENT)
    public void confirmPayment(@Variable int paymentId) {
        LoginResponse loginResponse = bankService.login(ACME_USERNAME, ACME_PASSWORD);
        ConfirmResponse confirmResponse = bankService.confirm(loginResponse.getSessionId(), paymentId);
        bankService.logout(loginResponse.getSessionId());

        if (!confirmResponse.isSuccess()) {
            log.warn("Payment confirmation failed for payment ID {}", paymentId);
        } else {
            log.info("Payment confirmation for payment ID {}", paymentId);
        }
    }

    @JobWorker(type = JOB_REQUEST_PAYMENT_REFUND)
    public void requestPaymentRefund(@Variable int paymentId) {
        LoginResponse loginResponse = bankService.login(ACME_USERNAME, ACME_PASSWORD);
        RefundResponse refundResponse = bankService.refund(loginResponse.getSessionId(), paymentId);
        bankService.logout(loginResponse.getSessionId());

        if (!refundResponse.isSuccess()) {
            log.warn("Payment refund failed for payment ID {}", paymentId);
        } else {
            log.info("Payment refund successful for payment ID {}", paymentId);
        }
    }
}
