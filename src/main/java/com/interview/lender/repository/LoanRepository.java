package com.interview.lender.repository;

import com.interview.lender.entity.Loan;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByCustomerNumber(String customerNumber);

    @Query("SELECT l FROM Loan l WHERE l.customerNumber = :customerNumber AND l.status IN ('PENDING', 'SCORING_IN_PROGRESS')")
    Optional<Loan> findOngoingLoanByCustomerNumber(String customerNumber);

    @Query("SELECT l FROM Loan l WHERE l.status = 'SCORING_IN_PROGRESS' AND l.retryCount < :maxRetries ORDER BY l.id DESC")
    List<Loan> findLoansForScoring(Integer maxRetries, Pageable pageable);
}
