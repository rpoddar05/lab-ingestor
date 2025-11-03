package com.phep.labingestor.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = false) // <-- reject unknown keys
public record LabEvent(
        String caseId, // can be null if not matched yet
        @NotBlank  String patientFirstName,
        @NotBlank String patientLastName,
        @NotNull LocalDate dob,
        @NotBlank String testCode,
        @NotBlank String resultValue,
        @NotBlank String resultStatus, //positive // negative //indeterminate
        @NotBlank String labName
) { }
