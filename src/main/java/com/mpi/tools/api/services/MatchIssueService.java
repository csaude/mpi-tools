package com.mpi.tools.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mpi.tools.api.model.MatchIssue;
import com.mpi.tools.api.repository.MatchIssueRepository;
import com.mpi.tools.api.repository.MatchedRecordRepository;

@Service
public class MatchIssueService {

	@Autowired
	private MatchIssueRepository matchIssueRepository;

	@Autowired
	private MatchedRecordRepository matchedRecordRepository;

	public MatchIssue save(MatchIssue record) {

		MatchIssue savedRecord = this.matchIssueRepository.findByOpenCrCruid(record.getOpenCrCruid());

		if (savedRecord == null) {
			savedRecord = matchIssueRepository.saveAndFlush(record);
		} else {

			if (!savedRecord.isProcessed()) {

				this.matchIssueRepository.delete(savedRecord);

				savedRecord.setMatchedRecords(record.getMatchedRecords());
				this.matchIssueRepository.save(record);
			}
			return savedRecord;
		}
		return savedRecord;

	}

	public List<MatchIssue> findByProcessed(boolean isProcessed) {
		return this.matchIssueRepository.findByIsProcessed(isProcessed);
	}
}
