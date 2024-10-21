package com.thinkbigdata.clevo.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thinkbigdata.clevo.controller.LearningController;
import com.thinkbigdata.clevo.controller.PostController;
import com.thinkbigdata.clevo.controller.SentenceController;
import com.thinkbigdata.clevo.controller.UserController;
import com.thinkbigdata.clevo.exception.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(assignableTypes = {LearningController.class, SentenceController.class, UserController.class, PostController.class})
public class ControllerHandler  {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> illegalArgument(IllegalArgumentException e) {
        return ResponseEntity.status(400).body(toMap(400, e.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> entityNotfound(EntityNotFoundException e) {
        return ResponseEntity.status(404).body(toMap(404, e.getMessage()));
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<?> restClient(RestClientException e) {
        System.out.println(e.getMessage());
        return ResponseEntity.status(500).body(toMap(500, e.getMessage()));
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<?> jsonProcess(JsonProcessingException e) {
        System.out.println(e.getMessage());
        return ResponseEntity.status(500).body(toMap(500, e.getMessage()));
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<?> duplicateEmail(DuplicateEmailException e) {
        return ResponseEntity.status(400).body(toMap(400, e.getMessage()));
    }

    @ExceptionHandler(InsufficientUserInfoException.class)
    public ResponseEntity<?> insufficientUser(InsufficientUserInfoException e) {
        return ResponseEntity.status(400).body(toMap(400, e.getMessage()));
    }

    @ExceptionHandler(RefreshTokenException.class)
    public ResponseEntity<?> refreshToken(RefreshTokenException e) {
        return ResponseEntity.status(401).body(toMap(401, e.getMessage()));
    }

    @ExceptionHandler(InvalidSessionException.class)
    public ResponseEntity<?> invalidSession(InvalidSessionException e) {
        return ResponseEntity.status(401).body(toMap(401, e.getMessage()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> usernameNotfound(UsernameNotFoundException e) {
        return ResponseEntity.status(400).body(toMap(400, e.getMessage()));
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<?> entityExists(EntityExistsException e) {
        return ResponseEntity.status(400).body(toMap(400, e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> accessDenied(AccessDeniedException e) {
        return ResponseEntity.status(403).body(toMap(403, e.getMessage()));
    }

    @ExceptionHandler(PronounceEvaluationException.class)
    public ResponseEntity<?> pronounceFail(PronounceEvaluationException e) {
        return ResponseEntity.status(500).body(toMap(403, e.getMessage()));
    }

    private Map<String, Object> toMap(Integer code, String message) {
        Map<String, Object> json = new HashMap<>();
        json.put("timestamp", LocalDateTime.now());
        json.put("status", code);
        json.put("message", message);
        return json;
    }
}
