package com.mpi.tools.api.services;

import org.springframework.stereotype.Service;

import com.mpi.tools.api.model.MatchIssue;
import com.mpi.tools.api.repository.MatchIssueRepository;

@Service
public class MatchIssueService {
	private MatchIssueRepository matchIssueRepository; 
	
	public MatchIssue save(MatchIssue record){
		MatchIssue savedRecord = matchIssueRepository.save(record);
	 	
		return savedRecord;
	}
}
