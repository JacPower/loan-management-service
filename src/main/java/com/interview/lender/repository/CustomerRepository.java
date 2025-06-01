package com.interview.lender.repository;

import com.interview.lender.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByCustomerNumber(String customerNumber);

    List<Customer> findByCustomerNumberIn(List<String> customerNumbers);

    boolean existsByCustomerNumber(String customerNumber);
}
