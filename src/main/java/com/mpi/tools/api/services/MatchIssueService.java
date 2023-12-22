package com.mpi.tools.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mpi.tools.api.model.MatchIssue;
import com.mpi.tools.api.repository.MatchIssueRepository;

@Service
public class MatchIssueService {

	@Autowired
	private MatchIssueRepository matchIssueRepository;

	public MatchIssue save(MatchIssue record) {

		MatchIssue savedRecord = this.matchIssueRepository.findByOpenCrCruid(record.getOpenCrCruid());

		if (savedRecord != null) {
			this.matchIssueRepository.delete(savedRecord);
		}
		
		savedRecord = matchIssueRepository.saveAndFlush(record);
		
		return savedRecord;

	}

	public List<MatchIssue> findFirstN_NotProcessedWithNoEmptyName(int n) {
		return this.matchIssueRepository.findFirstN_NotProcessedWithNoEmptyName(n);
	}

	public List<MatchIssue> getByMatchedRecordOpenmrsUuid(String openmrsUuid) {
		List<MatchIssue> issueOnDB =  this.matchIssueRepository.getByMatchedRecordOpenmrsUuid(openmrsUuid);
		
		return issueOnDB;
	}
	
	public MatchIssue getByMatchedRecordMpiId(String mpiId) {
		MatchIssue issueOnDB =  this.matchIssueRepository.getByMatchedRecordMpiId(mpiId);
		
		return issueOnDB;
	}
}
