package com.interview.lender.repository;

import com.interview.lender.entity.Loan;
import com.interview.lender.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoanRepository extends JpaRepository<Loan, UUID> {
    boolean existsByCustomerIdAndLoanStatus(UUID customerId, LoanStatus loanStatus);

    Optional<Loan> findByLoanCodeAndLoanStatus(String code, LoanStatus status);

    List<Loan> findByLoanStatus(LoanStatus status);

    List<Loan> findByLoanStatusAndDueDateBefore(LoanStatus status, LocalDate date);

    List<Loan> findByLoanStatusAndDueDateBetween(LoanStatus status, LocalDate startDate, LocalDate endDate);
}
