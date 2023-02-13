package com.dmdev.dao;

import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.integration.IntegrationTestBase;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static com.dmdev.entity.Provider.GOOGLE;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class SubscriptionDaoTestIT extends IntegrationTestBase {

    private final SubscriptionDao subscriptionDao = SubscriptionDao.getInstance();

    @Test
    void findAll() {
        Subscription subscription1 = subscriptionDao.insert(getSubscription("Ivan", 1));
        Subscription subscription2 = subscriptionDao.insert(getSubscription("Petr", 2));
        Subscription subscription3 = subscriptionDao.insert(getSubscription("Kolya", 3));

        List<Subscription> actualResult = subscriptionDao.findAll();

        assertThat(actualResult).hasSize(3);
        List<Integer> subscriptions = actualResult.stream()
                .map(Subscription::getId)
                .toList();
        assertThat(subscriptions).contains(subscription1.getId(), subscription2.getId(), subscription3.getId());
    }

    @Test
    void findById() {
        Subscription subscription = subscriptionDao.insert(getSubscription("Ivan", 1));

        Optional<Subscription> actualResult = subscriptionDao.findById(subscription.getId());

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(subscription);
    }

    @Test
    void deleteExisting() {
        Subscription subscription = subscriptionDao.insert(getSubscription("Ivan", 1));

        boolean actualResult = subscriptionDao.delete(subscription.getId());

        assertTrue(actualResult);
    }

    @Test
    void deleteNotExisting() {
        Subscription subscription = subscriptionDao.insert(getSubscription("Ivan", 1));

        boolean actualResult = subscriptionDao.delete(2);

        assertFalse(actualResult);
    }

    @Test
    void update() {
        Subscription subscription = getSubscription("Ivan", 1);
        subscriptionDao.insert(subscription);
        subscription.setExpirationDate(subscription.getExpirationDate().plus(10, ChronoUnit.DAYS));

        Subscription updatedSubscription = subscriptionDao.update(subscription);

        assertThat(updatedSubscription).isEqualTo(subscription);
    }

    @Test
    void insert() {
        Subscription dummy = getSubscription("Ivan", 1);

        Subscription actualResult = subscriptionDao.insert(dummy);

        assertNotNull(actualResult.getId());
    }

    @Test
    void findByUserId() {
        int dummyUserId = 1;
        Subscription subscription = subscriptionDao.insert(getSubscription("Ivan", dummyUserId));

        List<Subscription> actualResult = subscriptionDao.findByUserId(dummyUserId);

        assertThat(actualResult).hasSize(1);
        assertThat(actualResult.get(0)).isEqualTo(subscription);
    }

    @Test
    void shouldNotFindByUserIdIfUserNotExist() {
        subscriptionDao.insert(getSubscription("Ivan", 1));

        List<Subscription> actualResult = subscriptionDao.findByUserId(2);

        assertThat(actualResult).isEmpty();
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

}