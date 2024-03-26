package com.mgs.tipicobonuscampaign.adapt;

import com.mgs.tipicobonuscampaign.domain.model.Offer;
import com.mgs.tipicobonuscampaign.usecase.OfferService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController()
public class OffersController {

    OfferService offerService;

    public OffersController(OfferService offerService) {
        this.offerService = offerService;
    }

    @GetMapping
    public String helloWorld() {
        return "Hello World";
    }
    @GetMapping("/offers")
    public OffersResult getEligibleOffers(@RequestParam String customerUuid,
                                    @RequestParam String country,
                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate registrationDate,
                                    @RequestParam String depositAmount,
                                    @RequestParam Boolean isFirstDeposit) {
        BigDecimal parsedDepositAmount = new BigDecimal(depositAmount);
        List<Offer> eligibleOffers = offerService.getEligibleOffers(customerUuid, country, registrationDate, parsedDepositAmount, isFirstDeposit);
        List<OfferDTO> offerDTOS = eligibleOffers.stream().map(this::mapper).toList();
        return new OffersResult(offerDTOS);
    }
    private OfferDTO mapper(Offer offer) {
        return new OfferDTO(offer.getOfferUuid(), offer.getCampaign().getUuid(), offer.getExpirationDate());
    }
}
