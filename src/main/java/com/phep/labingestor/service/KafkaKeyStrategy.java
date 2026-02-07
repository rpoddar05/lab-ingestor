package com.phep.labingestor.service;

import com.phep.labingestor.model.LabEvent;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

/**
 * Computes Kafka message keys for LabEvent.
 * - If caseId exists: use it (good for internal replay/enriched events)
 * - Else: use hashed patient identity key to keep ordering per patient while avoiding hot partitions and PII.
 */

@Component
public class KafkaKeyStrategy {

    public String keyFor(LabEvent event){

        if (event == null) return "unknown";

        String caseId = event.caseId();

        if (caseId != null && !caseId.isBlank()) {
            return caseId.trim();
        }

        String first = safeLower(event.patientFirstName());
        String last  = safeLower(event.patientLastName());
        LocalDate dob = event.dob();

        String identity = first + "|" + last +  "|" + (dob != null ? dob : "");

        return "pid:" + sha256Hex(identity);
    }

    private String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(Character.forDigit((b >> 4) & 0xF, 16));
                sb.append(Character.forDigit(b & 0xF, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
           // throw new RuntimeException(e);
            // Extremely unlikely in modern JVMs; fallback to raw input
            return input;
        }
    }

    private String safeLower(@NotBlank String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }
}
