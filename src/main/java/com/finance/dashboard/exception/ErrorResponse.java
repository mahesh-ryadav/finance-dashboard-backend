package com.finance.dashboard.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private boolean success;
    private int status;

    private String errorCode;
    private String message;

    private Map<String, String> fieldErrors; // This id for validation errors

    private String path;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}