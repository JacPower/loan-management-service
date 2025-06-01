package com.interview.lender.services;

import com.interview.lender.dto.HttpResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@Service
@Slf4j
@RequiredArgsConstructor
public class RestClientService {
    private final RestTemplate restTemplate;



    public HttpResultDto sendRequest(String url, HttpMethod httpMethod, HttpHeaders headers, Object requestPayload, Class<?> responseClass) {
        try {
            HttpEntity<?> requestEntity = new HttpEntity<>(requestPayload, headers);
            ResponseEntity<?> response = null;
            if(httpMethod == null) {
               response = restTemplate.postForEntity(url, requestEntity, responseClass);
            } else {
                response = restTemplate.exchange(url, httpMethod, requestEntity, responseClass);
            }

            return HttpResultDto.builder()
                    .headers(response.getHeaders())
                    .success(true)
                    .statusCode(response.getStatusCode().value())
                    .message(response.getBody())
                    .build();
        } catch (HttpClientErrorException e) {
            log.error("HttpClientErrorException occurred | Status: {} | Body: {}", e.getStatusCode(), e.getResponseBodyAsString());

            return HttpResultDto.builder()
                    .statusCode(e.getStatusCode().value())
                    .error(e.getResponseBodyAsString())
                    .build();
        } catch (RestClientException e) {
            log.error("RestClientException occurred: ", e);

            return HttpResultDto.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .error(e.getMessage())
                    .build();
        }
    }
}
