package com.dmdev.service;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.exception.SubscriptionException;
import com.dmdev.exception.ValidationException;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.validator.CreateSubscriptionValidator;
import com.dmdev.validator.Error;
import com.dmdev.validator.ValidationResult;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.dmdev.entity.Provider.GOOGLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionDao subscriptionDao;
    @Mock
    private CreateSubscriptionMapper createSubscriptionMapper;
    @Mock
    private CreateSubscriptionValidator createSubscriptionValidator;
    @Spy
    private Clock clock;
    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    void expire() {
        Subscription subscription = getSubscription();
        doReturn(Optional.of(subscription)).when(subscriptionDao).findById(subscription.getId());

        subscriptionService.expire(subscription.getId());

        verify(subscriptionDao).update(subscription);
    }

    @Test
    void shouldThrowExceptionIfStatusExpired() {
        Subscription subscription = getSubscription();
        subscription.setStatus(Status.EXPIRED);
        doReturn(Optional.of(subscription)).when(subscriptionDao).findById(subscription.getId());

        assertThrows(SubscriptionException.class, () -> subscriptionService.cancel(subscription.getId()));
    }

    @Test
    void cancel() {
        Subscription subscription = getSubscription();
        doReturn(Optional.of(subscription)).when(subscriptionDao).findById(subscription.getId());

        subscriptionService.cancel(subscription.getId());

        verify(subscriptionDao).update(subscription);
    }

    @Test
    void shouldThrowExceptionIfStatusNotActive() {
        Subscription subscription = getSubscription();
        subscription.setStatus(Status.EXPIRED);
        doReturn(Optional.of(subscription)).when(subscriptionDao).findById(subscription.getId());

        assertThrows(SubscriptionException.class, () -> subscriptionService.expire(subscription.getId()));
    }

    @Test
    void shouldThrowExceptionIfSubscriptionIdInvalidInExpire() {
        int subscriptionDummyId = 0;
        doReturn(Optional.empty()).when(subscriptionDao).findById(subscriptionDummyId);

        assertThrows(IllegalArgumentException.class, () -> subscriptionService.expire(subscriptionDummyId));
    }
    @Test
    void shouldThrowExceptionIfSubscriptionIdInvalidInCancel() {
        int subscriptionDummyId = 0;
        doReturn(Optional.empty()).when(subscriptionDao).findById(subscriptionDummyId);

        assertThrows(IllegalArgumentException.class, () -> subscriptionService.cancel(subscriptionDummyId));
    }

    @Test
    void upsert() {
        CreateSubscriptionDto createSubscriptionDto = getCreateSubscription();
        Subscription subscription = getSubscription();
        doReturn(new ValidationResult()).when(createSubscriptionValidator).validate(createSubscriptionDto);
        doReturn(List.of(subscription)).when(subscriptionDao).findByUserId(createSubscriptionDto.getUserId());
        doReturn(subscription).when(subscriptionDao).upsert(subscription);

        Subscription actualResult = subscriptionService.upsert(createSubscriptionDto);

        assertThat(subscription).isEqualTo(actualResult);
        verify(subscriptionDao).upsert(subscription);
    }

    @Test
    void shouldThrowExceptionIfDtoInvalid() {
        CreateSubscriptionDto createSubscription = getCreateSubscription();
        ValidationResult validationResult = new ValidationResult();
        validationResult.add(Error.of(100, "userId is invalid"));
        doReturn(validationResult).when(createSubscriptionValidator).validate(createSubscription);

        assertThrows(ValidationException.class, () -> subscriptionService.upsert(createSubscription));
        verifyNoInteractions(subscriptionDao, createSubscriptionMapper);
    }

    private CreateSubscriptionDto getCreateSubscription() {
        return CreateSubscriptionDto.builder()
                .name("Ivan")
                .userId(1)
                .provider("GOOGLE")
                .expirationDate(Instant.now().plus(10, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS))
                .build();
    }

    private Subscription getSubscription() {
        return Subscription.builder()
                .id(1)
                .status(Status.ACTIVE)
                .expirationDate(Instant.now().plus(10, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS))
                .name("Ivan")
                .provider(GOOGLE)
                .userId(1)
                .build();
    }

}