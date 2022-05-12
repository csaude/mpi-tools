package com.mpi.tools.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mpi.tools.api.model.MatchConfig;

public interface MatchConfigRepository extends JpaRepository<MatchConfig, Long> {

	public List<MatchConfig> findByType(String type);
}
