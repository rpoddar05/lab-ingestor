package com.phep.labingestor.ingest.error;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IngestErrorRepository extends JpaRepository<IngestErrorEntity, UUID> {
}
