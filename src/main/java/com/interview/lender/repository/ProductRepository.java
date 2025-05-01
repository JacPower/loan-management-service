package com.interview.lender.repository;

import com.interview.lender.entity.Product;
import com.interview.lender.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByProductStatus(ProductStatus status);
}
