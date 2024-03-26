package com.mgs.tipicobonuscampaign.usecase;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DateService {

    private DateService() {
    }
    public LocalDate nowLocalDate() {
        return LocalDate.now();
    }
}
