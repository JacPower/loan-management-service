package com.interview.lender.repository;

import com.interview.lender.entity.ProductFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductFeeRepository extends JpaRepository<ProductFee, UUID> {
    boolean existsByProductIdAndFeeId(UUID productId, UUID feeId);
    List<ProductFee> findByProductId(UUID productId);
}
