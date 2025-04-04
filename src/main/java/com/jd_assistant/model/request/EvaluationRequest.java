package com.jd_assistant.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EvaluationRequest {
    private String jdText;
    private String question;
    private String answer;
}
