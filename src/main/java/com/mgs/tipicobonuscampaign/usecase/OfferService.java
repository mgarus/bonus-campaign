package com.mgs.tipicobonuscampaign.usecase;

import com.mgs.tipicobonuscampaign.domain.model.Campaign;
import com.mgs.tipicobonuscampaign.domain.model.Condition;
import com.mgs.tipicobonuscampaign.domain.model.Offer;
import com.mgs.tipicobonuscampaign.domain.repository.OfferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Service
public class OfferService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OfferService.class);
    private final OfferRepository offerRepository;
    private final DateService dateService;

    public OfferService(OfferRepository offerRepository, DateService dateService) {
        this.offerRepository = offerRepository;
        this.dateService = dateService;
    }

    public List<Offer> getEligibleOffers(String customerUuid, String customerCountry, LocalDate customerRegistrationDate, BigDecimal customerDepositAmount, Boolean isFirstDeposit) {
        LOGGER.info("Find eligible offers for customerUuid: {}, customerCountry: {}, customerRegistrationDate: {}, customerDepositAmount: {}",
                customerUuid, customerCountry, customerRegistrationDate, customerDepositAmount);
        List<Offer> offers = offerRepository.findOffersForCampaignByCustomerUuid(customerUuid);
        LOGGER.info("Offers found: {}", offers.size());
        if (offers.isEmpty()) return new ArrayList<>();
        return offers.stream()
                .filter(beforeExpirationDate())
                .filter(offer -> isRequestWithinCampaignDateRange(offer.getCampaign()))
                .filter(offer -> areAllConditionsMet(customerUuid, customerCountry, customerRegistrationDate,
                        customerDepositAmount, isFirstDeposit, offer.getCampaign()))
                .toList();
    }

    private boolean isRequestWithinCampaignDateRange(Campaign campaign) {
        LocalDate now = dateService.nowLocalDate();
        LOGGER.info("Campaign {} starts: {} and ends: {}. Request made on: {}",
                campaign.getName(), campaign.getStartDate(), campaign.getEndDate(), now);
        return now.isAfter(campaign.getStartDate()) && now.isBefore(campaign.getEndDate());
    }

    private boolean areAllConditionsMet(String customerUuid, String customerCountry, LocalDate customerRegistrationDate, BigDecimal customerDepositAmount, Boolean isFirstDeposit, Campaign campaign) {
        return campaign.getConditions().stream()
                .allMatch(condition -> meetsConditions(customerUuid, condition, customerCountry, customerRegistrationDate, customerDepositAmount, isFirstDeposit));
    }

    private Predicate<Offer> beforeExpirationDate() {
        return offer -> {
            LOGGER.info("Offer {} expiration date is: {}", offer.getOfferUuid(), offer.getExpirationDate());
            LocalDate now = dateService.nowLocalDate();
            return now.isBefore(offer.getExpirationDate());
        };
    }

    private boolean meetsConditions(String customerUuid, Condition condition,
                                    String customerCountry,
                                    LocalDate customerRegistrationDate,
                                    BigDecimal customerDepositAmount,
                                    boolean isFirstDeposit) {
        return switch (condition.getName()) {
            case "country" -> customerCountry.equalsIgnoreCase(condition.getValuee());
            case "registrationDate" -> {
                LocalDate conditionDate = LocalDate.parse(condition.getValuee());
                yield customerRegistrationDate.isBefore(conditionDate);
            }
            case "minimumDepositAmount" -> {
                BigDecimal conditionMinDepositAmount = new BigDecimal(condition.getValuee());
                yield conditionMinDepositAmount.compareTo(customerDepositAmount) <= 0;
            }
            case "firstDeposit" -> isFirstDeposit == Boolean.parseBoolean(condition.getValuee());
            case "customerUuid" -> customerUuid.equalsIgnoreCase(condition.getValuee());
            default -> false;
        };
    }

}
