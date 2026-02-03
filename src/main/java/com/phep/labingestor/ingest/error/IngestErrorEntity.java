package com.phep.labingestor.ingest.error;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "ingest_error")
public class IngestErrorEntity {

    @Id
    private UUID id;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private String endpoint;

    @Column(name = "correlation_id")
    private String correlationId;

    @Column(name = "error_type", nullable = false)
    private String errorType;

    @Column(name = "error_message", nullable = false, columnDefinition = "text")
    private String errorMessage;

    @Column(columnDefinition = "text")
    private String payload;

    @Column(nullable = false)
    private String status;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = OffsetDateTime.now();
        if (source == null) source = "http";
        if (status == null) status = "NEW";
    }

}
