package com.interview.lender.services;

import com.interview.lender.dto.ResponseDto;
import com.interview.lender.exception.BusinessException;
import com.interview.lender.util.Util;
import com.interview.lender.dto.GlobalFiltersDto;
import com.interview.lender.dto.PaginatedResponseDto;
import com.interview.lender.dto.CustomerDto;
import com.interview.lender.entity.Customer;
import com.interview.lender.enums.CustomerStatus;
import com.interview.lender.repository.CustomerRepository;
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
public class CustomerProcessor {
    private final CustomerRepository customerRepository;



    @Transactional
    public ResponseDto createCustomer(CustomerDto request) {
        customerRepository.findByEmailOrPhoneOrIdNumber(request.getEmail(), request.getPhone(), request.getIdNumber())
                .ifPresent(existingCustomer -> {
                    String message = "Customer with similar email, phone or id number already exist";
                    String logMessage = message + " | email: %s | Phone: %s | Id Number: %s".formatted(request.getEmail(), request.getPhone(), request.getIdNumber());
                    log.error(logMessage);
                    throw BusinessException.alreadyExist(message);
                });
        var customer = Customer.builder()
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .gender(request.getGender())
                .email(request.getEmail())
                .phone(request.getPhone())
                .idNumber(request.getIdNumber())
                .dob(request.getDob())
                .loanLimit(request.getLoanLimit())
                .description(request.getDescription())
                .billingCycle(request.getBillingCycle())
                .preferredBillingDay(request.getPreferredBillingDay())
                .preferredChannels(request.getPreferredChannels())
                .accountStatus(CustomerStatus.ACTIVE)
                .build();

        customerRepository.save(customer);

        return Util.buildResponse("Customer created", CREATED, customer);
    }



    public ResponseDto getCustomerById(GlobalFiltersDto filtersDto) {
        Customer customer = customerRepository.findById(filtersDto.getId())
                .orElseThrow(() -> BusinessException.notFound("Customer not found with id: " + filtersDto.getId()));

        return Util.buildResponse("Customer found", OK, customer);
    }



    public ResponseDto getAllCustomers(GlobalFiltersDto filtersDto) {
        Pageable pageable = PageRequest.of(filtersDto.getPage(), filtersDto.getSize());
        List<Customer> list = customerRepository.findAll(pageable).getContent();
        String message = !list.isEmpty() ? "Customers found" : "Customer records not found";
        PaginatedResponseDto paginatedResponse = Util.buildPaginatedResponse(list, pageable);

        return Util.buildResponse(message, OK, paginatedResponse);
    }



    @Transactional
    public ResponseDto updateCustomer(CustomerDto requestDto) {
        Customer customer = customerRepository.findById(requestDto.getId())
                .orElseThrow(() -> BusinessException.notFound("Customer not found with id: " + requestDto.getId()));
        customer.setFirstName(requestDto.getFirstName());
        customer.setMiddleName(requestDto.getMiddleName());
        customer.setLastName(requestDto.getLastName());
        customer.setGender(requestDto.getGender());
        customer.setEmail(requestDto.getEmail());
        customer.setPhone(requestDto.getPhone());
        customer.setIdNumber(requestDto.getIdNumber());
        customer.setDob(requestDto.getDob());
        customer.setLoanLimit(requestDto.getLoanLimit());
        customer.setDescription(requestDto.getDescription());
        customer.setBillingCycle(requestDto.getBillingCycle());
        customer.setPreferredBillingDay(requestDto.getPreferredBillingDay());
        customer.setPreferredChannels(requestDto.getPreferredChannels());
        customer.setAccountStatus(requestDto.getStatus());

        var updatedCustomer = customerRepository.save(customer);

        return Util.buildResponse("Customer updated", OK, updatedCustomer);
    }
}
