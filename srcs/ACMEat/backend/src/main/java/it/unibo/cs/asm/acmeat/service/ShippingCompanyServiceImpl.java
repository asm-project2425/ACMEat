package it.unibo.cs.asm.acmeat.service;

import it.unibo.cs.asm.acmeat.dto.entities.ShippingCompanyDTO;
import it.unibo.cs.asm.acmeat.model.ShippingCompany;
import it.unibo.cs.asm.acmeat.model.Coordinate;
import it.unibo.cs.asm.acmeat.integration.GISService;
import it.unibo.cs.asm.acmeat.repository.ShippingCompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ShippingCompanyServiceImpl implements ShippingCompanyService {
    private final ShippingCompanyRepository shippingCompanyRepository;
    private final GISService GISService;

    @Override
    public ShippingCompany getShippingCompanyById(int id) {
        return shippingCompanyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Shipping company not found with id: " + id));
    }

    @Override
    public List<ShippingCompanyDTO> getShippingCompanies(Coordinate restaurantPosition) {
        List<ShippingCompanyDTO> result = new ArrayList<>();
        for (ShippingCompany company : shippingCompanyRepository.findAll()) {
            double distance = GISService.distanceBetween(restaurantPosition, company.getPosition());
            if (distance <= 10000) {
                result.add(new ShippingCompanyDTO(company));
            }
        }
        return result;
    }
}
