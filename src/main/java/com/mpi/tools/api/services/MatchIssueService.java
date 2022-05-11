package com.mpi.tools.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mpi.tools.api.model.MatchIssue;
import com.mpi.tools.api.repository.MatchIssueRepository;

@Service
public class MatchIssueService {
	@Autowired
	private MatchIssueRepository matchIssueRepository; 
	
	public MatchIssue save(MatchIssue record){
		MatchIssue savedRecord = matchIssueRepository.saveAndFlush(record);
	 	
		return savedRecord;
	}
}
