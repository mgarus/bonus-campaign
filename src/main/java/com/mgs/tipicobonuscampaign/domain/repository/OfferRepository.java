package com.mgs.tipicobonuscampaign.domain.repository;

import com.mgs.tipicobonuscampaign.domain.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {

    @Query("SELECT o FROM Offer o JOIN o.campaign c WHERE o.campaign IN ( " +
            "    SELECT c FROM Campaign c JOIN c.conditions cond " +
            "    WHERE cond.name = 'customerUuid' AND cond.valuee = :customerUuid) " +
            "    AND o.campaign.id = c.id")
    List<Offer> findOffersForCampaignByCustomerUuid(@Param("customerUuid") String customerUuid);
}
