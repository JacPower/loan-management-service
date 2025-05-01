package com.interview.lender.repository;

import com.interview.lender.entity.Payment;
import com.interview.lender.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByLoanId(UUID loanId);
    List<Payment> findByLoanIdAndPaymentStatus(UUID loanId, PaymentStatus status);

}
