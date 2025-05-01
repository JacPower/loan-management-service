package com.interview.lender.services;

import com.interview.lender.dto.ResponseDto;
import com.interview.lender.exception.BusinessException;
import com.interview.lender.exception.ResourceNotFoundException;
import com.interview.lender.repository.FeeRepository;
import com.interview.lender.util.Util;
import com.interview.lender.dto.FeeDto;
import com.interview.lender.dto.GlobalFiltersDto;
import com.interview.lender.dto.PaginatedResponseDto;
import com.interview.lender.dto.ProductDto;
import com.interview.lender.entity.Fee;
import com.interview.lender.entity.Product;
import com.interview.lender.entity.ProductFee;
import com.interview.lender.repository.ProductFeeRepository;
import com.interview.lender.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductProcessor {
    private final ProductRepository productRepository;
    private final FeeRepository feeRepository;
    private final ProductFeeRepository productFeeRepository;



    @Transactional
    public ResponseDto createProduct(ProductDto productDto) {
        var product = Product.builder()
                .productName(productDto.getProductName())
                .description(productDto.getDescription())
                .tenureType(productDto.getTenureType())
                .tenureValue(productDto.getTenureValue())
                .daysAfterDueForLateFee(productDto.getDaysAfterDueForLateFee())
                .productStatus(productDto.getProductStatus())
                .isFixedTerm(productDto.getIsFixedTerm())
                .isNotificationEnabled(productDto.getIsNotificationEnabled())
                .build();

        var savedProduct = productRepository.save(product);

        return Util.buildResponse("Product created", CREATED, savedProduct);
    }



    @Transactional
    public ResponseDto createFee(FeeDto requestDto) {
        var fee = Fee.builder()
                .feeName(requestDto.getFeeName())
                .feeType(requestDto.getFeeType())
                .calculationType(requestDto.getCalculationType())
                .feeValue(requestDto.getFeeValue())
                .applicationTiming(requestDto.getApplicationTiming())
                .description(requestDto.getDescription())
                .isActive(requestDto.getIsActive())
                .build();

        var savedFee = feeRepository.save(fee);

        return Util.buildResponse("Fee created", CREATED, savedFee);
    }



    @Transactional
    public ResponseDto createProductFee(GlobalFiltersDto filtersDto) {
        boolean exists = productFeeRepository.existsByProductIdAndFeeId(filtersDto.getProductId(), filtersDto.getFeeId());

        if (exists) {
            log.error("Product fee already exists for | Product Id: {} | Fee Id: {}", filtersDto.getProductId(), filtersDto.getFeeId());
            throw BusinessException.alreadyExist("Product fee already exists");
        }

        var optionalProduct = productRepository.findById(filtersDto.getProductId());
        var optionalFee = feeRepository.findById(filtersDto.getFeeId());

        if (optionalProduct.isEmpty() || optionalFee.isEmpty()) {
            log.error("Product or Fee not found for given IDs | Product Id: {} | Fee Id: {}", filtersDto.getProductId(), filtersDto.getFeeId());
            throw new ResourceNotFoundException("Product or Fee not found");
        }

        var productFee = ProductFee.builder()
                .product(optionalProduct.get())
                .fee(optionalFee.get())
                .build();

        productFeeRepository.save(productFee);

        var productWithFees = productRepository.getReferenceById(filtersDto.getProductId());

        return Util.buildResponse("Product fee created", CREATED, productWithFees);
    }



    public ResponseDto getProductById(GlobalFiltersDto filtersDto) {
        Product product = productRepository.findById(filtersDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + filtersDto.getId()));

        return Util.buildResponse("Product found", OK, product);
    }



    public ResponseDto getAllProducts(GlobalFiltersDto filtersDto) {
        Pageable pageable = PageRequest.of(filtersDto.getPage(), filtersDto.getSize());
        List<Product> list = productRepository.findAll(pageable).getContent();
        String message = !list.isEmpty() ? "Products found" : "Products records not found";
        PaginatedResponseDto paginatedResponse = Util.buildPaginatedResponse(list, pageable);

        return Util.buildResponse(message, OK, paginatedResponse);
    }



    public ResponseDto getFeeById(GlobalFiltersDto globalFiltersDto) {
        Fee fee = feeRepository.findById(globalFiltersDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Fee not found with id: " + globalFiltersDto.getId()));

        return Util.buildResponse("Fee found", OK, fee);
    }



    public ResponseDto getAllFees(GlobalFiltersDto filtersDto) {
        Pageable pageable = PageRequest.of(filtersDto.getPage(), filtersDto.getSize());
        List<Fee> list = feeRepository.findAll(pageable).getContent();
        String message = !list.isEmpty() ? "Fees found" : "Fees records not found";
        PaginatedResponseDto paginatedResponse = Util.buildPaginatedResponse(list, pageable);

        return Util.buildResponse(message, OK, paginatedResponse);
    }



    @Transactional
    public ResponseDto updateProduct(ProductDto requestDto) {
        Product product = productRepository.findById(requestDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + requestDto.getId()));

        product.setProductName(requestDto.getProductName());
        product.setDescription(requestDto.getDescription());
        product.setTenureType(requestDto.getTenureType());
        product.setTenureValue(requestDto.getTenureValue());
        product.setDaysAfterDueForLateFee(requestDto.getDaysAfterDueForLateFee());
        product.setProductStatus(requestDto.getProductStatus());
        product.setIsFixedTerm(requestDto.getIsFixedTerm());
        product.setUpdatedAt(LocalDateTime.now());

        var updatedProduct = productRepository.save(product);

        return Util.buildResponse("Product updated", OK, updatedProduct);
    }



    @Transactional
    public ResponseDto updateFee(FeeDto requestDto) {
        Fee fee = feeRepository.findById(requestDto.getId())
                .orElseThrow(() -> {
                    log.error("Fee not found with id: {}", requestDto.getId());
                    return new ResourceNotFoundException("Fee not found");
                });

        fee.setFeeName(requestDto.getFeeName());
        fee.setFeeType(requestDto.getFeeType());
        fee.setCalculationType(requestDto.getCalculationType());
        fee.setFeeValue(requestDto.getFeeValue());
        fee.setApplicationTiming(requestDto.getApplicationTiming());
        fee.setIsActive(requestDto.getIsActive());
        fee.setUpdatedAt(LocalDateTime.now());

        var updatedFee = feeRepository.save(fee);

        return Util.buildResponse("Product fee updated", OK, updatedFee);
    }
}
