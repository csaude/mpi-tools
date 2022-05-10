package com.mpi.tools.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mpi.tools.api.model.MatchedRecord;

public interface MatchedRecordRepository extends JpaRepository<MatchedRecord,Long> {
}
