package com.mgs.tipicobonuscampaign.adapt;

import com.mgs.tipicobonuscampaign.domain.model.Campaign;
import com.mgs.tipicobonuscampaign.domain.model.Condition;
import com.mgs.tipicobonuscampaign.domain.model.Offer;
import com.mgs.tipicobonuscampaign.domain.repository.OfferRepository;
import com.mgs.tipicobonuscampaign.usecase.OfferService;
import com.mgs.tipicobonuscampaign.usecase.DateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

    @Mock
    private OfferRepository offerRepository;
    @Mock
    private DateService dateService;
    @InjectMocks
    private OfferService offerService;

    @Test
    void should_find_no_offers_for_not_matching_customer() {
        //Given
        String notMatchingCustomer = "adc";
        LocalDate campaignStartDate = LocalDate.of(2024, 3, 24);
        when(offerRepository.findOffersForCampaignByCustomerUuid(notMatchingCustomer))
                .thenReturn(List.of());

        //when
        List<Offer> eligibleOffers = offerService.getEligibleOffers(notMatchingCustomer, "PL", campaignStartDate, new BigDecimal("1"), true);

        //then
        assertThat(eligibleOffers).isEmpty();
    }

    @Test
    void should_find_no_offers_for_matching_user_and_future_campaign() {
        //Given
        String matchingCustomer = "abc";
        LocalDate campaignStartDate = LocalDate.of(2024, 3, 24);
        when(offerRepository.findOffersForCampaignByCustomerUuid(matchingCustomer))
                .thenReturn(List.of(getOfferForCampaignForDay(campaignStartDate)));
        LocalDate dayBeforeCampaignStartDate = getFixedCurrentDate(campaignStartDate.minusDays(1));
        when(dateService.nowLocalDate()).thenReturn(dayBeforeCampaignStartDate);

        //when
        List<Offer> eligibleOffers = offerService.getEligibleOffers(matchingCustomer, "PL", campaignStartDate, new BigDecimal("1"), true);

        //then
        assertThat(eligibleOffers).isEmpty();
    }

    @Test
    void shouldFind_NoOffers_forMatchingCustomer_WithNotEnoughDeposit_and_CampaignWithinRequestDate() {
        //Given
        String matchingCustomer = "abc";
        LocalDate campaignStartDate = LocalDate.of(2024, 3, 24);
        when(offerRepository.findOffersForCampaignByCustomerUuid(matchingCustomer))
                .thenReturn(List.of(getOfferForCampaignForDay(campaignStartDate)));
        LocalDate dayAfterCampaignStartDate = getFixedCurrentDate(campaignStartDate.plusDays(1));
        when(dateService.nowLocalDate()).thenReturn(dayAfterCampaignStartDate);

        //when
        BigDecimal notEnoughCustomerDepositAmount = new BigDecimal("1");
        List<Offer> eligibleOffers = offerService.getEligibleOffers(matchingCustomer, "PL", campaignStartDate, notEnoughCustomerDepositAmount, true);

        //then
        assertThat(eligibleOffers).isEmpty();
    }

    @Test
    void shouldFind_OneOffer_forMatchingCustomer_WithEnoughDeposit_and_CampaignWithinRequestDate() {
        //Given
        String matchingCustomer = "abc";
        LocalDate campaignStartDate = LocalDate.of(2024, 3, 24);
        when(offerRepository.findOffersForCampaignByCustomerUuid(matchingCustomer))
                .thenReturn(List.of(getOfferForCampaignForDay(campaignStartDate)));
        LocalDate dayAfterCampaignStartDate = getFixedCurrentDate(campaignStartDate.plusDays(1));
        when(dateService.nowLocalDate()).thenReturn(dayAfterCampaignStartDate);

        //when
        BigDecimal enoughCustomerDepositAmount = new BigDecimal("10");
        List<Offer> eligibleOffers = offerService.getEligibleOffers(matchingCustomer, "PL", campaignStartDate, enoughCustomerDepositAmount, true);

        //then
        assertThat(eligibleOffers.size()).isOne();
    }

    private static LocalDate getFixedCurrentDate(LocalDate mockDate) {
        LocalDateTime localDateTime = LocalDateTime.of(mockDate, LocalTime.MIDNIGHT);
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
        Clock fixedClock = Clock.fixed(instant, ZoneOffset.UTC.normalized());
        return LocalDate.now(fixedClock);
    }

    private Offer getOfferForCampaignForDay(LocalDate day) {
        Campaign campaign = new Campaign();
        campaign.setName("Next Day");
        campaign.setStartDate(day);
        campaign.setEndDate(day.plusDays(7));

        List<Condition> conditions = getConditions();
        campaign.setConditions(conditions);

        Offer offer1 = new Offer();
        offer1.setExpirationDate(day.plusDays(7));
        offer1.setCampaign(campaign);
        return offer1;
    }
    private static List<Condition> getConditions() {
        List<Condition> conditions = new ArrayList<>(2);
        Condition matchingCustomerCondition = new Condition();
        matchingCustomerCondition.setName("customerUuid");
        matchingCustomerCondition.setValuee("abc");

        Condition minimumDepositAmountCondition = new Condition();
        minimumDepositAmountCondition.setName("minimumDepositAmount");
        minimumDepositAmountCondition.setValuee("10");

        conditions.add(matchingCustomerCondition);
        conditions.add(minimumDepositAmountCondition);
        return conditions;
    }

}