package com.mpi.tools.api.repository;

import org.springframework.data.repository.CrudRepository;

import com.mpi.tools.api.model.MatchedRecord;

public interface MatchRecordRepository extends CrudRepository<MatchedRecord, Long> {

}
