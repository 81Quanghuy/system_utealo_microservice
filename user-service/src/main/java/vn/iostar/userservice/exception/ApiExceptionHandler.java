package vn.iostar.userservice.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import vn.iostar.userservice.dto.response.GenericResponse;
import vn.iostar.userservice.exception.wrapper.BadRequestException;
import vn.iostar.userservice.exception.wrapper.ForbiddenException;
import vn.iostar.userservice.exception.wrapper.NotFoundException;
import vn.iostar.userservice.exception.wrapper.UnsupportedMediaTypeException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ApiExceptionHandler {

    @ExceptionHandler(value = {
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class
    })
    public <T extends BindException> ResponseEntity<GenericResponse> handleValidationException(final T e) {
        log.info("ApiExceptionHandler, ResponseEntity<GenericResponse> handleValidationException");
        final var badRequest = HttpStatus.BAD_REQUEST;
        List<ObjectError> objectErrors = e.getBindingResult().getAllErrors();
        Map<String, String> errors = new HashMap<>();
        objectErrors.forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        return new ResponseEntity<>(
                GenericResponse.builder()
                        .success(false)
                        .message("Xảy ra lỗi khi xử lý dữ liệu!")
                        .result(errors)
                        .statusCode(badRequest.value())
                        .build(), badRequest);
    }

    @ExceptionHandler(value = {
            BadRequestException.class
    })
    public <T extends RuntimeException> ResponseEntity<GenericResponse> handleBadRequestException(final T e) {
        log.info("ApiExceptionHandler, ResponseEntity<GenericResponse> handleApiRequestException");
        return ResponseEntity.badRequest().body(
                GenericResponse.builder()
                        .success(false)
                        .message(e.getMessage())
                        .result(null)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build());
    }

    @ExceptionHandler(value = {
            NotFoundException.class
    })
    public <T extends RuntimeException> ResponseEntity<GenericResponse> handleNotFoundException(final T e) {
        log.info("ApiExceptionHandler, ResponseEntity<GenericResponse> handleNotFoundException");
        final var notFound = HttpStatus.NOT_FOUND;

        return new ResponseEntity<>(
                GenericResponse.builder()
                        .success(false)
                        .message(e.getMessage())
                        .result(null)
                        .statusCode(notFound.value())
                        .build(), notFound);
    }

    @ExceptionHandler(value = {
            UnsupportedMediaTypeException.class
    })
    public <T extends RuntimeException> ResponseEntity<GenericResponse> handleUnsupportedMediaTypeException(final T e) {
        log.info("ApiExceptionHandler, ResponseEntity<GenericResponse> handleUnsupportedMediaTypeException");
        final var unsupportedMediaType = HttpStatus.UNSUPPORTED_MEDIA_TYPE;

        return new ResponseEntity<>(
                GenericResponse.builder()
                        .success(false)
                        .message(e.getMessage())
                        .result(null)
                        .statusCode(unsupportedMediaType.value())
                        .build(), unsupportedMediaType);
    }

    @ExceptionHandler(value = {
            ForbiddenException.class
    })
    public <T extends RuntimeException> ResponseEntity<GenericResponse> handleForbiddenException(final T e) {
        log.info("ApiExceptionHandler, ResponseEntity<GenericResponse> handleForbiddenException");
        final var forbidden = HttpStatus.FORBIDDEN;

        return new ResponseEntity<>(
                GenericResponse.builder()
                        .success(false)
                        .message(e.getMessage())
                        .result(null)
                        .statusCode(forbidden.value())
                        .build(), forbidden);
    }

    @ExceptionHandler(value = {
            NullPointerException.class,
            Exception.class,
            RuntimeException.class,
            IllegalArgumentException.class
    })
    public <T extends RuntimeException> ResponseEntity<GenericResponse> handleServerException(final T e) {
        log.info("ApiExceptionHandler, ResponseEntity<GenericResponse> handleServerException");
        final var internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;

        return new ResponseEntity<>(
                GenericResponse.builder()
                        .success(false)
                        .message(e.getMessage())
                        .result("Internal Server Error")
                        .statusCode(internalServerError.value())
                        .build(), internalServerError);
    }

    @ExceptionHandler(value = {
            MissingRequestHeaderException.class
    })
    public <T extends MissingRequestHeaderException> ResponseEntity<GenericResponse> handleMissingRequestHeaderException(final T e) {
        log.info("ApiExceptionHandler, ResponseEntity<GenericResponse> handleMissingRequestHeaderException");
        final var internalServerError = HttpStatus.UNAUTHORIZED;

        return new ResponseEntity<>(
                GenericResponse.builder()
                        .success(false)
                        .message("Access token is missing!")
                        .result("Unauthorized")
                        .statusCode(internalServerError.value())
                        .build(), internalServerError);
    }
}
