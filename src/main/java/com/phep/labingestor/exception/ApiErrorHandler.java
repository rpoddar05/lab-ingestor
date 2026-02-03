package com.phep.labingestor.exception;

import com.phep.labingestor.ingest.error.IngestErrorEntity;
import com.phep.labingestor.ingest.error.IngestErrorRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
class ApiErrorHandler {

    private final IngestErrorRepository ingestErrorRepository;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> onValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> Map.of("field", e.getField(), "message", e.getDefaultMessage()))
                .toList();

        saveError(req,
                "ValidationError",
                "Validation failed: " + errors,
                extractPayload(req));

        // quarantine response
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(Map.of(
                        "error", "QUARANTINED_VALIDATION_ERROR",
                        "detail", "Payload quarantined for review",
                        "fieldErrors", errors
                ));

    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<?> onBadJson(HttpMessageNotReadableException ex, HttpServletRequest req) {

        String detail = mostSpecificMessage(ex);

        saveError(req,
                "HttpMessageNotReadableException",
                detail,
                extractPayload(req));


        // quarantine response
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(Map.of(
                        "error", "QUARANTINED_INVALID_PAYLOAD",
                        "detail", "Malformed JSON or unknown enum/value. Payload quarantined for review.",
                        "cause", detail
                ));
    }

    // ---------------- private helpers ----------------

    private void saveError(HttpServletRequest req, String type, String message, String payload) {
        IngestErrorEntity e = new IngestErrorEntity();
        e.setEndpoint(req.getMethod() + " " + req.getRequestURI());
        e.setCorrelationId(req.getHeader("X-Correlation-Id"));
        e.setErrorType(type);
        e.setErrorMessage(message);
        e.setPayload(payload);
        ingestErrorRepository.save(e);
    }

    private String mostSpecificMessage(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null) cur = cur.getCause();
        return cur.getMessage();
    }

    private String extractPayload(HttpServletRequest req) {
        if (req instanceof ContentCachingRequestWrapper wrapper) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf != null && buf.length > 0) {
                return new String(buf, StandardCharsets.UTF_8);
            }
        }
        return null; // payload may be null if not wrapped (we’ll ensure wrapper exists via filter)
    }
}