package com.mpi.tools.api.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mpi.tools.api.dto.matched.Matched;
import com.mpi.tools.api.dto.matched.patient.IdentifierDTO;
import com.mpi.tools.api.dto.matched.patient.LinkDTO;
import com.mpi.tools.api.dto.matched.patient.PatientHeaderDTO;
import com.mpi.tools.api.dto.matched.santempi.MatchResource;
import com.mpi.tools.api.model.MatchConfig;
import com.mpi.tools.api.model.MatchIssue;
import com.mpi.tools.api.model.MatchStatus;
import com.mpi.tools.api.model.MatchedRecord;
import com.mpi.tools.api.resource.interfaces.MatchIssueFeignClient;
import com.mpi.tools.api.resource.interfaces.MatchedPatientFeignClient;
import com.mpi.tools.api.resource.interfaces.santempi.SanteMpiMatchIssueFeignClient;

@Service
public class MatchedRecordService {
	
	@Autowired
	private MatchIssueService matchIssueService;
	
	@Autowired
	private MatchConfigService matchConfig;
	
	private final static String UUID_CODE = "http://metadata.epts.e-saude.net/dictionary/patient-uuid";
	
	private final static String NID_CODE = "http://metadata.epts.e-saude.net/dictionary/patient-identifiers/nid-tarv";
	
	public final static String MALE = "male";
	
	public final static String FEMALE = "female";
	
	public final static String MALE_CONVERTED = "M";
	
	public final static String FEMALE_CONVERTED = "F";
	
	private final static String LOAD = "LOAD";
	
	public final Log logger = LogFactory.getLog(getClass());
	
	public String saveMatchedInfo(Matched matchList, MatchConfig config, MatchedPatientFeignClient client,
	        MatchIssueFeignClient matchIssueClient) {
		
		logger.info("============================= Generate Match Patient=========================================");
		
		for (PatientHeaderDTO patient : matchList.getEntry()) {
			this.createMatchedIssue(patient);
		}
	
		logger.info("============================= Done Generate Match Patient=========================================");
		
		return "done";
	}
	
	@SuppressWarnings("unused")
	private String getCustomNextPage(Matched matchList) {
		for (LinkDTO nextPage : matchList.getLink()) {
			if (nextPage.getRelation().equals("next")) {
				if (!nextPage.getUrl().isEmpty()) {
					
					String actualCount = nextPage.getUrl().split("_count=")[1].toString();
					
					String countCount = actualCount.split("&")[0];
					
					return countCount;
				}
			}
		}
		return null;
	}

	// Formar os objectos
	private void createMatchedIssue(PatientHeaderDTO patient) {
		
		// Antes de process
		
		MatchIssue matchIssue = new MatchIssue();
		matchIssue.setOpenmrsUuid(MatchedRecordService.getUUID(patient.getResource().getIdentifier()));
		matchIssue.setOpenCrCruid(patient.getResource().getId());
		matchIssue.setDateCreated(new Date());
		// Sett Main match
		matchIssue.setBirthDate(patient.getResource().getBirthDate());
		matchIssue.setTarvNid(MatchedRecordService.getNID(patient.getResource().getIdentifier()));
		matchIssue.setOpenmrsUuid(MatchedRecordService.getUUID(patient.getResource().getIdentifier()));
		matchIssue.setDateCreated(new Date());
		matchIssue.setGender(this.convertGender(patient.getResource().getGender()));
		
		if (!patient.getResource().getName().isEmpty()) {
			if (!patient.getResource().getName().get(0).getGiven().isEmpty()) {
				matchIssue.setGivenName(patient.getResource().getName().get(0).getGiven().get(0));
			}
			
			matchIssue.setFamilyName(patient.getResource().getName().get(0).getFamily());
			
		}
		
		// Set main Match
		// sett Matched Patients
		matchIssue.setBirthDate(patient.getResource().getBirthDate());
		
		// DOT IT LETTER
		this.matchIssueService.save(matchIssue);
		
	}
	
	private boolean hasUuid(MatchIssue matchIssue, com.mpi.tools.api.dto.matched.matched.issue.MatchIssue matched) {
		
		for (MatchedRecord matchedRecord : matchIssue.getMatchedRecords()) {
			if (matched.getUid().equals(matchedRecord.getOpenmrsUuid())) {
				return true;
			}
		}
		return false;
	}
	
	private List<? extends com.mpi.tools.api.dto.matched.matched.issue.MatchIssue> getMatchedPatients(String patientId,
	        SanteMpiMatchIssueFeignClient client) {
		
		List<? extends com.mpi.tools.api.dto.matched.matched.issue.MatchIssue> matchIssueDTOs = new ArrayList<>();
		
		logger.info("Patient id " + patientId);
		
		//matchIssueDTOs = client.getMatchedPatients(patientId);
		
		MatchResource mr = client.getMatchedPatients(patientId);
		
		matchIssueDTOs = mr.parseToIssue(client, matchIssueService);
		
		logger.info("patient matche fetched");
		
		return matchIssueDTOs;
	}
	
	private String convertGender(String gender) {
		
		if (gender != null && gender.equals(FEMALE)) {
			
			return FEMALE_CONVERTED;
		} else if (gender != null && gender.equals(MALE)) {
			
			return MALE_CONVERTED;
		}
		return null;
	}
	
	private MatchedRecord createMatchRecord(com.mpi.tools.api.dto.matched.matched.issue.MatchIssue matchedPatient) {
		
		MatchedRecord matchedRecord = new MatchedRecord();
		
		matchedRecord.setBirthDate(matchedPatient.getBirthDate());
		matchedRecord.setOpencr_cruid(matchedPatient.getId());
		matchedRecord.setTarvNid(matchedPatient.getNid_tarv());
		matchedRecord.setOpenmrsUuid(matchedPatient.getUid());
		matchedRecord.setDateCreated(new Date());
		matchedRecord.setGender(this.convertGender(matchedPatient.getGender()));
		
		matchedRecord.setGivenName(matchedPatient.getGiven());
		
		matchedRecord.setFamilyName(matchedPatient.getFamily());
		
		return matchedRecord;
	}
	
	public static String getUUID(List<IdentifierDTO> identifiers) {
		
		for (IdentifierDTO identifier : identifiers) {
			if (identifier.getSystem().equals(UUID_CODE)) {
				return identifier.getValue();
			}
		}
		
		return null;
	}
	
	public static String getNID(List<IdentifierDTO> identifiers) {
		
		for (IdentifierDTO identifier : identifiers) {
			if (identifier.getSystem().equals(NID_CODE)) {
				return identifier.getValue();
			}
		}
		return null;
	}
	
	public void saveUnapliedMatchInfo(List<MatchIssue> unapliedMatchInfos, SanteMpiMatchIssueFeignClient client) {
		
		logger.info("=========================Stating to Process Unplied Match ====================================");
		
		int i = 0;
		
		for (MatchIssue unapliedMatchInfo : unapliedMatchInfos) {
			// sett Matched Patients
			List<? extends com.mpi.tools.api.dto.matched.matched.issue.MatchIssue> matchIssueDTOs = new ArrayList<>();
			
			try {
				i++;
				
				logger.info("Processing record [" + i + "] id: " + unapliedMatchInfo.getId() + ", uuid: "
				        + unapliedMatchInfo.getOpenmrsUuid() + ", cruid: " + unapliedMatchInfo.getOpenCrCruid());
				
				matchIssueDTOs = this.getMatchedPatients(unapliedMatchInfo.getOpenCrCruid().toString(), client);
				
				if (matchIssueDTOs.size() == 1) {
					logger.info("No match found for record [" + i + "] id: " + unapliedMatchInfo.getId() + ", uuid: "
					        + unapliedMatchInfo.getOpenmrsUuid() + ", cruid: " + unapliedMatchInfo.getOpenCrCruid());
					
					unapliedMatchInfo.setStatus(MatchStatus.NO_MATCH_FOUND);
				} else {
					for (com.mpi.tools.api.dto.matched.matched.issue.MatchIssue matched : matchIssueDTOs) {
						
						if (this.hasUuid(unapliedMatchInfo, matched)) {
							continue;
						}
						
						logger.info("Matched Patient link - " + matched.getNid_tarv());
						logger.info("Matched patient id - " + unapliedMatchInfo.getOpenCrCruid());
						
						MatchedRecord matchedRecord = this.createMatchRecord(matched);
						matchedRecord.setMatchIssue(unapliedMatchInfo);
						
						unapliedMatchInfo.addMatcheRecords(matchedRecord);
					}
					
					unapliedMatchInfo.setStatus(MatchStatus.PROCESSED_SUCCESSFULLY);
				}
				
			}
			catch (Exception e) {
				// Create here unplied mathc patients
				logger.info(
				    "An error ocurred while processing record [" + i + "] id: " + unapliedMatchInfo.getId() + ", uuid: "
				            + unapliedMatchInfo.getOpenmrsUuid() + ", cruid: " + unapliedMatchInfo.getOpenCrCruid());
				
				e.printStackTrace();
				
				unapliedMatchInfo.setStatus(MatchStatus.PROCESSED_WITH_ERROR);
				
				//tryToRestartService(unapliedMatchInfo);
			}
			
			if (hasExistingMatch(unapliedMatchInfo)) {
				logger.info("There is another match issue with exact same matched records! Ignoring!");
			} else {
				this.matchIssueService.save(unapliedMatchInfo);
			}
		}
		
		logger.info("=========================== Unplied Match Process Finished ====================================");
		
	}
	
	private boolean hasExistingMatch(MatchIssue newMatchIssue) {
		List<MatchedRecord> matchedRecords = newMatchIssue.getMatchedRecords();
		
		for (MatchedRecord record : matchedRecords) {
			List<MatchIssue> existingMatches = matchIssueService.getByMatchedRecordOpenmrsUuid(record.getOpenmrsUuid());
			
			for (MatchIssue issueOnDB : existingMatches) {
				if (newMatchIssue.hasExactMatchedRecords(issueOnDB)) {
					return true;
				}
			}
			
		}
		
		return false;
	}
	
	@SuppressWarnings("unused")
	private void tryToRestartService(MatchIssue unapliedMatchInfo) {
		String[] cmd = new String[] { "/bin/bash", "./restart-service.sh", "" + unapliedMatchInfo.getId() };
		
		try {
			logger.info("Restarting the service");
			
			Runtime.getRuntime().exec(cmd);
			
			return;
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public MatchConfig openLoadPage() {
		
		MatchConfig load = this.matchConfig.findMatchConfig(LOAD);
		
		if (load == null) {
			MatchConfig config = new MatchConfig();
			
			config.setType(LOAD);
			config.setActive(Boolean.TRUE);
			config.setLastPage("NO");
			config.setDateCreated(new Date());
			
			this.matchConfig.createMatchConfig(config);
			return config;
		}
		
		return load;
	}
	
	public void closeLoadPage(MatchConfig config) {
		
		config.setActive(Boolean.FALSE);
		config.setLastPage(String.valueOf(0));
		
		this.matchConfig.updateConfig(config);
		
	}
}
