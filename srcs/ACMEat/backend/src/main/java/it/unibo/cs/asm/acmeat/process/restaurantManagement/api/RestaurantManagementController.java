package it.unibo.cs.asm.acmeat.process.restaurantManagement.api;

import it.unibo.cs.asm.acmeat.dto.response.RequestRestaurantInformationResponse;
import it.unibo.cs.asm.acmeat.process.restaurantManagement.worker.AcmeRMWorkers;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("api/v1/restaurants")
@RestController
public class RestaurantManagementController {
    private final AcmeRMWorkers acmeRMWorkers;

    @GetMapping("/{restaurantId}/information")
    public ResponseEntity<RequestRestaurantInformationResponse> retrieveRestaurantInformation(
            @PathVariable int restaurantId) {
        return ResponseEntity.ok(acmeRMWorkers.retrieveRestaurantInformationManually(restaurantId));
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirmRestaurantInformationUpdate(@RequestParam String correlationKey) {
        acmeRMWorkers.confirmRestaurantInformationUpdateManually(correlationKey);
        return ResponseEntity.noContent().build();
    }
}
