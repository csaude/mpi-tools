package com.mpi.tools.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mpi.tools.api.model.MatchIssue;

public interface MatchIssueRepository extends JpaRepository<MatchIssue, Long> {

	public MatchIssue findByOpenCrCruid(String openCrCruid);

	@Query("select m from MatchIssue m WHERE givenName != null and familyName != null and (status = com.mpi.tools.api.model.MatchStatus.NOT_PROCESSED or status = com.mpi.tools.api.model.MatchStatus.PROCESSED_WITH_ERROR)")
	public List<MatchIssue> findAllNotProcessedWithNoEmptyName();

	@Query("select mi from MatchedRecord mr JOIN mr.matchIssue mi where mr.openmrsUuid = :openmrsUuid")
	public List<MatchIssue> getByMatchedRecordOpenmrsUuid(@Param("openmrsUuid")String openmrsUuid);
}