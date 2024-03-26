package com.mgs.tipicobonuscampaign.domain.repository;

import com.mgs.tipicobonuscampaign.domain.model.Condition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConditionRepository extends JpaRepository<Condition, Long> {

    Optional<Condition> findByValuee(String valuee);
}
