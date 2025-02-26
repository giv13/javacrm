package ru.giv13.infocrm.system;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record Response(boolean success, Object error, Object data, long timestamp) {
    public static ResponseEntity<Response> ok(Object data) {
        return new ResponseEntity<>(new Response(true, null, data, System.currentTimeMillis()), HttpStatus.OK);
    }

    public static ResponseEntity<Response> er(Object error, HttpStatus status) {
        return new ResponseEntity<>(new Response(false, error, null, System.currentTimeMillis()), status);
    }
}
