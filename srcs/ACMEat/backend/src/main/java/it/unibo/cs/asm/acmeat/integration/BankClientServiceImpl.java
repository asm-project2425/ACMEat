package it.unibo.cs.asm.acmeat.integration;

import it.unibo.cs.asm.soap.bankclient.*;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Holder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BankClientServiceImpl implements BankClientService {
    private final Bank bank;

    public BankClientServiceImpl(@Value("${soap.bank.endpoint}") String endpointUrl) {
        BankService service = new BankService();
        this.bank = service.getBankServicePort();
        ((BindingProvider) bank).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);
    }

    @Override
    public LoginResponse login(String username, String password) {
        Holder<Boolean> successHolder = new Holder<>();
        Holder<String> sessionIdHolder = new Holder<>();
        bank.login(password, username, successHolder, sessionIdHolder);
        LoginResponse response = new LoginResponse();
        response.setSuccess(successHolder.value);
        response.setSessionId(sessionIdHolder.value);
        return response;
    }

    @Override
    public CreatePaymentResponse createPayment(String sessionId, double amount, int orderId) {
        Holder<Boolean> successHolder = new Holder<>();
        Holder<Integer> paymentIdHolder = new Holder<>();
        bank.createPayment(amount, orderId, sessionId, successHolder, paymentIdHolder);
        CreatePaymentResponse response = new CreatePaymentResponse();
        response.setSuccess(successHolder.value);
        response.setPaymentId(paymentIdHolder.value);
        return response;
    }

    @Override
    public VerifyTokenResponse verifyToken(String sessionId, String token) {
        VerifyTokenResponse response = new VerifyTokenResponse();
        response.setSuccess(bank.verifyToken(sessionId, token));
        return response;
    }

    @Override
    public RefundResponse refund(String sessionId, int paymentId) {
        RefundResponse response = new RefundResponse();
        response.setSuccess(bank.refund(paymentId, sessionId));
        return response;
    }

    @Override
    public ConfirmResponse confirm(String sessionId, int paymentId) {
        ConfirmResponse response = new ConfirmResponse();
        response.setSuccess(bank.confirm(paymentId, sessionId));
        return response;
    }

    @Override
    public void logout(String sessionId) {
        bank.logout(sessionId);
    }
}
