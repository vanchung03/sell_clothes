package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import jakarta.persistence.EntityNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Xử lý ngoại lệ khi không tìm thấy Entity trong cơ sở dữ liệu
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails("Không tìm thấy tài nguyên", ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    // Xử lý ngoại lệ khi tham số không hợp lệ
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails("Tham số không hợp lệ", ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // Xử lý ngoại lệ MethodArgumentNotValidException (lỗi validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        // Lấy BindingResult từ ngoại lệ
        BindingResult result = ex.getBindingResult();
        StringBuilder errorMessage = new StringBuilder("Lỗi xác thực: ");

        // Duyệt qua các lỗi của từng trường và thêm vào thông báo
        for (FieldError fieldError : result.getFieldErrors()) {
            errorMessage.append(fieldError.getField())
                    .append(": ")
                    .append(fieldError.getDefaultMessage())
                    .append("; ");
        }

        // Tạo chi tiết lỗi và trả về
        ErrorDetails errorDetails = new ErrorDetails("Lỗi xác thực", errorMessage.toString());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // Xử lý các ngoại lệ chung không được xử lý cụ thể
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails("Lỗi hệ thống", ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
