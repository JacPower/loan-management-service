package com.interview.lender.repository;

import com.interview.lender.entity.CustomerNotificationChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerNotificationChannelRepository extends JpaRepository<CustomerNotificationChannel, UUID> {
    List<CustomerNotificationChannel> findByCustomerId(UUID customerId);

    void deleteByCustomerIdAndChannel(UUID customerId, String channel);

    boolean existsByCustomerIdAndChannel(UUID customerId, String channel);
}
