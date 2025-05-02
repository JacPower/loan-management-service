package com.interview.lender.rule;

import com.interview.lender.dto.ResponseDto;
import com.interview.lender.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RuleDelegator {
    private final List<IRule> rules;



    public ResponseDto routeRequest(Object businessRule, Object ruleOperation, Object requestPayload) {
        return rules.stream()
                .filter(rule -> rule.canExecute(businessRule))
                .findFirst()
                .map(rule -> rule.execute(ruleOperation, requestPayload))
                .orElseThrow(() -> {
                    var message = "No handler found for request type: " + businessRule;
                    log.error(message);
                    return BusinessException.unknownOperation(message);
                });
    }
}
