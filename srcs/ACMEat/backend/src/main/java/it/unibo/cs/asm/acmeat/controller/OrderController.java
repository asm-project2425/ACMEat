package it.unibo.cs.asm.acmeat.controller;

import it.unibo.cs.asm.acmeat.dto.response.GetOrderStatusResponse;
import it.unibo.cs.asm.acmeat.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("api/v1")
@RestController
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/orders/{orderId}/status")
    public ResponseEntity<GetOrderStatusResponse> getOrderStatus(@PathVariable int orderId) {
        String orderStatus = String.valueOf(orderService.getOrderById(orderId).getStatus());
        return ResponseEntity.ok(new GetOrderStatusResponse(orderId, orderStatus));
    }
}
