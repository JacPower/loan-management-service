package com.interview.lender.rule;

import com.interview.lender.dto.ResponseDto;

public interface IRule {
    boolean canExecute(Object businessRule);

    ResponseDto execute(Object businessRuleOperation, Object operationRequestPayload);
}
