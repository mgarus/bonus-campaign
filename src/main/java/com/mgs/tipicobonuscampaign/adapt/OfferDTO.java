package com.mgs.tipicobonuscampaign.adapt;

import java.time.LocalDate;
import java.util.UUID;

public class OfferDTO {

    public OfferDTO(UUID offerUuid, UUID campaignUuid, LocalDate expirationDate) {
        this.offerUuid = offerUuid;
        this.campaignUuid = campaignUuid;
        this.expirationDate = expirationDate;
    }

    private final UUID offerUuid;
    private final UUID campaignUuid;
    private final LocalDate expirationDate;

    public UUID getOffer() {
        return offerUuid;
    }

    public UUID getCampaign() {
        return campaignUuid;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }
}
