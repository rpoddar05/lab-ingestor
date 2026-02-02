package com.phep.labingestor.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ResultStatus {
    POSITIVE,
    NEGATIVE,
    INDETERMINATE;

    @JsonCreator
    public static ResultStatus forValue(String value) {
        if(value == null) return null;
        return ResultStatus.valueOf(value.trim().toUpperCase());

    }

    @JsonValue
    public String toJson() {
        return name();
    }
}
