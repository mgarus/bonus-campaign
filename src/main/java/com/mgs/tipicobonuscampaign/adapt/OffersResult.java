package com.mgs.tipicobonuscampaign.adapt;

import java.util.List;

public record OffersResult(List<OfferDTO> offers) {

    public String getName() {
        return "offers";
    }
}