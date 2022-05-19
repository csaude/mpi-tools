package com.mpi.tools.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mpi.tools.api.model.MatchIssue;

public interface MatchIssueRepository extends JpaRepository<MatchIssue, Long> {

	public MatchIssue findByOpenCrCruid(String openCrCruid);

	public List<MatchIssue> findByIsProcessed(boolean isProcessed);
}
