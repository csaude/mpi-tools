package com.mpi.tools.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mpi.tools.api.model.MatchConfig;

public interface MatchConfigRepository extends JpaRepository<MatchConfig, Long> {

	public MatchConfig findByType(String type);
}
