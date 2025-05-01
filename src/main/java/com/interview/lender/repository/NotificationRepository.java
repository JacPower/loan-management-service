package com.interview.lender.repository;

import com.interview.lender.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByCustomerId(UUID customerId);
    List<Notification> findByLoanId(UUID loanId);
    List<Notification> findByStatus(String status);
}
