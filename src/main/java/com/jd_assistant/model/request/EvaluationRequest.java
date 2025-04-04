package com.jd_assistant.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EvaluationRequest {
    private String jdText;
    private String question;
    private String answer;
}
