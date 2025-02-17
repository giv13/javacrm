package ru.giv13.infocrm.system;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionHandlerAdvice {
    @ExceptionHandler(HttpMessageNotReadableException.class)
    private ResponseEntity<Response> badRequestException(Exception exception) {
        return Response.er(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    private ResponseEntity<Response> authenticationException(Exception exception) {
        return Response.er(exception.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    private ResponseEntity<Response> unauthorizedException() {
        return Response.er("Для доступа к этому ресурсу требуется аутентификация", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(JwtException.class)
    private ResponseEntity<Response> jwtException(Exception exception) {
        return Response.er("Просроченный или недействительный JWT", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    private ResponseEntity<Response> forbiddenException() {
        return Response.er("Доступ запрещен", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    private ResponseEntity<Response> methodNotAllowedException(Exception exception) {
        Pattern pattern = Pattern.compile(" '.+'");
        Matcher matcher = pattern.matcher(exception.getMessage());
        return Response.er("Метод" + (matcher.find() ? matcher.group() : "") + " не поддерживается", HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(Exception.class)
    private ResponseEntity<Response> internalServerErrorException(Exception exception) {
        return Response.er(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
