package com.phep.labingestor.model;

import java.time.LocalDate;

public record LabEvent(
        String caseId, // can be null if not matched yet
        String patientFirstName,
        String patientLastName,
        LocalDate dob,
        String testCode,
        String resultValue,
        String resultStatus, //positive // negative //indeterminate
        String labName
) { }
