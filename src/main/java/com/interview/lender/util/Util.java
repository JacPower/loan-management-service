package com.interview.lender.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.lender.dto.ResponseDto;
import com.interview.lender.dto.PaginatedResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
public class Util {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final DecimalFormat df = new DecimalFormat("#.00");



    private Util() {
        throw new IllegalStateException("Utility class instantiation not allowed.");
    }



    public static <T> Optional<T> convertToDto(Object source, Class<T> dtoClass) {
        try {
            if (source instanceof String jsonString) {
                if (jsonString.trim().equals("{}")) {
                    return Optional.empty();
                }
                return Optional.ofNullable(objectMapper.readValue(jsonString, dtoClass));
            }
            return Optional.ofNullable(objectMapper.convertValue(source, dtoClass));
        } catch (Exception e) {
            log.error("Failed to convert json string to dto: ", e);
            return Optional.empty();
        }
    }



    public static String toJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert dto to json string: ", e);
            return "{}";
        }
    }



    public static ResponseDto buildResponse(String message, HttpStatus status, Object payload) {
        return ResponseDto.builder()
                .httpStatus(status)
                .resultCode(status.value())
                .success(true)
                .message(message)
                .data(payload)
                .build();
    }



    /**
     * Serializing PageImpl instances as-is, is not supported, meaning that there is no guarantee about the stability
     * of the resulting JSON structure! For a stable JSON structure, this method transforms the PageImpl into a
     * PaginatedResponseDto. This ensures a consistent and stable JSON structure for API responses.
     * Example of serializing a PageImpl directly (this is not recommended for stable JSON structure):
     * PageImpl<PaymentRequestsDto> page = new PageImpl<>(dtoList, pageable, dtoList.size());
     * return new ResponseEntity<>(page, HttpStatus.OK);
     * The above code would serialize the PageImpl as a raw `PageImpl` object, leading to an unstable JSON response
     * structure with no guarantee that the pagination metadata will be serialized consistently.
     * Alternative approaches include:
     * - Using Spring Data's PagedModel (globally via @EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)).
     * - Using Spring HATEOAS and Spring Data's PagedResourcesAssembler as documented in:
     * <a href="https://docs.spring.io/spring-data/commons/reference/repositories/core-extensions.html#core.web.pageables">...</a>.
     */
    public static <T> PaginatedResponseDto buildPaginatedResponse(List<T> dtoList, Pageable pageable) {
        Page<T> page = new PageImpl<>(dtoList, pageable, dtoList.size());

        return PaginatedResponseDto.builder()
                .content(Collections.singletonList(dtoList))
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    public static double roundToTwoDecimals(double value) {
        return Double.parseDouble(df.format(value));
    }
}
