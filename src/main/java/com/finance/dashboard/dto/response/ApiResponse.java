package com.finance.dashboard.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)// This will hide null fields from json
public class ApiResponse<T>{

    private boolean success;
    private String message;
    private T data;
    private String errorCode;

    private LocalDateTime timestamp = LocalDateTime.now();

    // Static factory methods for success and error

    public static <T> ApiResponse<T> success(String message,T data){
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(String message){
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .build();
    }


    public static <T> ApiResponse<T> success(String message, String errorCode){
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .build();
    }


}
