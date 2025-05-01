package com.interview.lender.repository;

import com.interview.lender.entity.LoanFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Repository
public interface LoanFeeRepository extends JpaRepository<LoanFee, UUID> {
    Set<LoanFee> findByLoanId(UUID loanId);
    boolean existsByLoanIdAndFeeIdAndAppliedAtBetween(UUID loanId, UUID feeId, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
