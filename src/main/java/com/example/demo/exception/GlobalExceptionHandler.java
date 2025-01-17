package com.example.demo.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Xử lý ngoại lệ khi không tìm thấy Entity trong cơ sở dữ liệu.
     * @param ex Ngoại lệ EntityNotFoundException được ném ra.
     * @param request Yêu cầu Web mà ngoại lệ xảy ra.
     * @return Đối tượng ResponseEntity chứa thông tin lỗi và HTTP status 404 (NOT_FOUND).
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        // Tạo chi tiết lỗi với thông báo cụ thể
        ErrorDetails errorDetails = new ErrorDetails("Resource not found", ex.getMessage());
        // Trả về lỗi với mã trạng thái HTTP 404
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    /**
     * Xử lý ngoại lệ khi có tham số không hợp lệ được truyền vào ứng dụng.
     * @param ex Ngoại lệ IllegalArgumentException được ném ra.
     * @param request Yêu cầu Web mà ngoại lệ xảy ra.
     * @return Đối tượng ResponseEntity chứa thông tin lỗi và HTTP status 400 (BAD_REQUEST).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        // Tạo chi tiết lỗi với thông báo cụ thể
        ErrorDetails errorDetails = new ErrorDetails("Invalid argument", ex.getMessage());
        // Trả về lỗi với mã trạng thái HTTP 400
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    /**
     * Xử lý các ngoại lệ chung không được xử lý cụ thể bởi các handler khác.
     * @param ex Ngoại lệ bất kỳ được ném ra.
     * @param request Yêu cầu Web mà ngoại lệ xảy ra.
     * @return Đối tượng ResponseEntity chứa thông tin lỗi và HTTP status 500 (INTERNAL_SERVER_ERROR).
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
        // Tạo chi tiết lỗi với thông báo chung
        ErrorDetails errorDetails = new ErrorDetails("Internal server error", ex.getMessage());
        // Trả về lỗi với mã trạng thái HTTP 500
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
