package com.interview.lender.repository;

import com.interview.lender.entity.Installment;
import com.interview.lender.enums.InstallmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface InstallmentRepository extends JpaRepository<Installment, UUID> {
    List<Installment> findByLoanIdAndInstallmentStatusOrderByDueDate(UUID loanId, InstallmentStatus status);
    long countByLoanIdAndInstallmentStatusAndDueDateBefore(UUID loanId, InstallmentStatus status, LocalDate date);
}
