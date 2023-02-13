package com.dmdev.validator;

import com.dmdev.dto.CreateSubscriptionDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class CreateSubscriptionValidatorTest {

    private final CreateSubscriptionValidator createSubscriptionValidator = CreateSubscriptionValidator.getInstance();

    @Test
    void shouldPassValidation() {
        CreateSubscriptionDto given = CreateSubscriptionDto.builder()
                .name("Ivan")
                .userId(1)
                .provider("GOOGLE")
                .expirationDate(Instant.now().plus(10, ChronoUnit.DAYS))
                .build();

        ValidationResult actualResult = createSubscriptionValidator.validate(given);

        assertFalse(actualResult.hasErrors());
    }

    @Test
    void userIdIsNull() {
        CreateSubscriptionDto given = CreateSubscriptionDto.builder()
                .name("Ivan")
                .userId(null)
                .provider("GOOGLE")
                .expirationDate(Instant.now().plus(10, ChronoUnit.DAYS))
                .build();

        ValidationResult actualResult = createSubscriptionValidator.validate(given);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(100);
    }

    @Test
    void nameIsBlank() {
        CreateSubscriptionDto given = CreateSubscriptionDto.builder()
                .name("")
                .userId(1)
                .provider("GOOGLE")
                .expirationDate(Instant.now().plus(10, ChronoUnit.DAYS))
                .build();

        ValidationResult actualResult = createSubscriptionValidator.validate(given);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(101);
    }

    @Test
    void invalidProvider() {
        CreateSubscriptionDto given = CreateSubscriptionDto.builder()
                .name("Ivan")
                .userId(1)
                .provider("FAKE")
                .expirationDate(Instant.now().plus(10, ChronoUnit.DAYS))
                .build();

        ValidationResult actualResult = createSubscriptionValidator.validate(given);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(102);
    }

    @Test
    void invalidExpirationDate() {
        CreateSubscriptionDto given = CreateSubscriptionDto.builder()
                .name("Ivan")
                .userId(1)
                .provider("GOOGLE")
                .expirationDate(Instant.now().minus(10, ChronoUnit.DAYS))
                .build();

        ValidationResult actualResult = createSubscriptionValidator.validate(given);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(103);
    }

    @Test
    void nullExpirationDate() {
        CreateSubscriptionDto given = CreateSubscriptionDto.builder()
                .name("Ivan")
                .userId(1)
                .provider("GOOGLE")
                .expirationDate(null)
                .build();

        ValidationResult actualResult = createSubscriptionValidator.validate(given);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(103);
    }

    @Test
    void invalidExpirationDateProviderUserIdName() {
        CreateSubscriptionDto given = CreateSubscriptionDto.builder()
                .name("")
                .userId(null)
                .provider("FAKE")
                .expirationDate(null)
                .build();

        ValidationResult actualResult = createSubscriptionValidator.validate(given);

        assertThat(actualResult.getErrors()).hasSize(4);

        List<Integer> codes = actualResult.getErrors().stream()
                .map(Error::getCode)
                .toList();

        assertThat(codes).contains(100, 101, 102, 103);
    }
}