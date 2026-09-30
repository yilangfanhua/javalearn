package com.example.demo.exception;

import com.example.demo.common.ApiResponse;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import java.lang.IllegalArgumentException;
import org.springframework.security.authentication.BadCredentialsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@RestControllerAdvice 
public class GlobaExceptionHandle {

    private static final Logger log =
        LoggerFactory.getLogger(GlobaExceptionHandle.class);


    @ExceptionHandler (UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFound(UserNotFoundException exception){
        return ResponseEntity.status(404).body(ApiResponse.<Void>error(404,exception.getMessage()));
    }

    @ExceptionHandler (MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException exception){
        
        String message=exception.getBindingResult().getFieldErrors().stream().findFirst().map(error->error.getField()+":"+error.getDefaultMessage()).orElse("请求参数不合法");
        
        return ResponseEntity.badRequest().body(ApiResponse.<Void>error(400,message));
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnreadableBody() {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.<Void>error(400, "请求体格式不正确"));
    }
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateKey() {
        return ResponseEntity
                .status(409)
                .body(ApiResponse.<Void>error(409, "用户 ID 已存在"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleOtherException(Exception e) {
        e.printStackTrace();
        log.error("unhandled application exception", e);

        return ResponseEntity
                .status(500)
                .body(ApiResponse.<Void>error(500, "服务器内部错误"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(
            IllegalArgumentException exception) {

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.<Void>error(
                        400,
                        exception.getMessage()
                ));
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials() {
        return ResponseEntity
                .status(401)
                .body(ApiResponse.<Void>error(
                        401,
                        "用户名或密码错误"
                ));
    }

    

}
