package ru.giv13.infocrm.system;

import io.jsonwebtoken.JwtException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionHandlerAdvice implements ResponseBodyAdvice<Object> {
    @ExceptionHandler(HttpMessageNotReadableException.class)
    private Response<String> onHttpMessageNotReadableException(Exception exception) {
        return Response.er(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    private Response<String> onAuthenticationException(Exception exception) {
        return Response.er(exception.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    private Response<String> onInsufficientAuthenticationException() {
        return Response.er("Для доступа к этому ресурсу требуется аутентификация", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(JwtException.class)
    private Response<String> onJwtException(Exception exception) {
        return Response.er("Просроченный или недействительный JWT", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    private Response<String> onAuthorizationDeniedException() {
        return Response.er("Доступ запрещен", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    private Response<String> onNoResourceFoundException() {
        return Response.er("Ресурс не найден", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    private Response<String> onHttpRequestMethodNotSupportedException(Exception exception) {
        Pattern pattern = Pattern.compile(" '.+'");
        Matcher matcher = pattern.matcher(exception.getMessage());
        return Response.er("Метод" + (matcher.find() ? matcher.group() : "") + " не поддерживается", HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<Map<String, String>> onMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), StringUtils.capitalize(Objects.requireNonNullElse(error.getDefaultMessage(), "")));
        }
        return Response.er(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Response<Map<String, String>> onConstraintViolationException(ConstraintViolationException exception) {
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            errors.put(violation.getPropertyPath().toString(), StringUtils.capitalize(Objects.requireNonNullElse(violation.getMessage(), "")));
        }
        return Response.er(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    private Response<String> onException(Exception exception) {
        return Response.er(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, @NonNull MethodParameter returnType, @NonNull MediaType selectedContentType, @NonNull Class selectedConverterType, @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
        if (body instanceof Response<?>) {
            response.setStatusCode(HttpStatus.valueOf(((Response<?>) body).status()));
            return body;
        }
        return Response.ok(body);
    }
}
