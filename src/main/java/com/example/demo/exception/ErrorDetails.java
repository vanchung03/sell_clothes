package com.example.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDetails {
    private String error;
    private String message;
}
