package com.dmdev.service;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.integration.IntegrationTestBase;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.validator.CreateSubscriptionValidator;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static com.dmdev.entity.Provider.GOOGLE;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

public class SubscriptionServiceIT extends IntegrationTestBase {

    private SubscriptionService subscriptionService;
    private SubscriptionDao subscriptionDao;

    @BeforeEach
    void init() {
        subscriptionDao = new SubscriptionDao();
        subscriptionService = new SubscriptionService(
                subscriptionDao, CreateSubscriptionMapper.getInstance(), CreateSubscriptionValidator.getInstance(), Clock.systemUTC()
        );
    }

    @Test
    void upsertIfSubscriptionByUserExists() {
        Subscription subscription = subscriptionDao.insert(getSubscription("Ivan", 1));

        Subscription actualResult = subscriptionService.upsert(getCreateSubscription());

        assertThat(actualResult).isEqualTo(subscription);
    }

    @Test
    void upsertIfSubscriptionByUserDoesNotExists() {
        Subscription subscription = subscriptionDao.insert(getSubscription("Ivan", 1));
        CreateSubscriptionDto createSubscription = CreateSubscriptionDto.builder()
                .name("Petr")
                .userId(2)
                .provider("GOOGLE")
                .expirationDate(Instant.now().plus(10, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS))
                .build();

        Subscription actualResult = subscriptionService.upsert(createSubscription);

        assertNotNull(actualResult.getId());
    }

    @Test
    void cancel() {
        Subscription subscription = subscriptionDao.insert(getSubscription("Ivan", 1));

        subscriptionService.cancel(subscription.getId());


    }

    @Test
    void expire() {
        Subscription subscription = subscriptionDao.insert(getSubscription("Ivan", 1));

        subscriptionService.expire(subscription.getId());
    }
    private Subscription getSubscription(String name, int userId) {
        return Subscription.builder()
                .status(Status.ACTIVE)
                .expirationDate(Instant.now().plus(10, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS))
                .name(name)
                .provider(GOOGLE)
                .userId(userId)
                .build();
    }

    private CreateSubscriptionDto getCreateSubscription() {
        return CreateSubscriptionDto.builder()
                .name("Ivan")
                .userId(1)
                .provider("GOOGLE")
                .expirationDate(Instant.now().plus(10, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS))
                .build();
    }
}
