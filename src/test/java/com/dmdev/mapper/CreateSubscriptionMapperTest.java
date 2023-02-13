package com.dmdev.mapper;

import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import org.h2.engine.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.exceptions.verification.MoreThanAllowedActualInvocations;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.dmdev.entity.Provider.GOOGLE;
import static org.assertj.core.api.Assertions.assertThat;

class CreateSubscriptionMapperTest {

    CreateSubscriptionMapper createSubscriptionMapper = CreateSubscriptionMapper.getInstance();

    @Test
    void map() {
        CreateSubscriptionDto given = CreateSubscriptionDto.builder()
                .name("Ivan")
                .userId(1)
                .provider("GOOGLE")
                .expirationDate(Instant.now().plus(10, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS))
                .build();

        Subscription actualResult = createSubscriptionMapper.map(given);
        Subscription expectedResult = Subscription.builder()
                .id(null)
                .status(Status.ACTIVE)
                .expirationDate(Instant.now().plus(10, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS))
                .name("Ivan")
                .provider(GOOGLE)
                .userId(1)
                .build();
        assertThat(expectedResult).isEqualTo(actualResult);
    }

}