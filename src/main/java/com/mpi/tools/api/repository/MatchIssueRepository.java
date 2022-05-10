package com.mpi.tools.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mpi.tools.api.model.MatchIssue;

public interface MatchIssueRepository extends JpaRepository<MatchIssue,Long> {
}
