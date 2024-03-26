package com.mgs.tipicobonuscampaign.domain.repository;

import com.mgs.tipicobonuscampaign.domain.model.Campaign;
import com.mgs.tipicobonuscampaign.domain.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignJpaRepository extends JpaRepository<Campaign, Long> {
    @Query("SELECT DISTINCT o FROM Campaign c " +
            "LEFT JOIN c.offers o ON c.id=o.campaign.id " +
            "LEFT JOIN c.conditions cond ON c.id=cond.campaign.id " +
            "WHERE cond.campaign.id=o.campaign.id " +
            "AND cond.name = 'customerUuid' " +
            "AND cond.valuee = :customerUuid")
    List<Offer> findOffersForCampaignByCustomerId(@Param("customerUuid") String customerUuid);

}
