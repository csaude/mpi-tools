package com.mpi.tools.api.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import com.mpi.tools.api.dto.matched.MatchIssueDTO;
import com.mpi.tools.api.dto.matched.PatientMatchedDTO;
import com.mpi.tools.api.dto.matched.UserDTO;
import com.mpi.tools.api.dto.patient.CodeDTO;
import com.mpi.tools.api.dto.patient.IdentifierDTO;
import com.mpi.tools.api.dto.patient.LinkDTO;
import com.mpi.tools.api.dto.patient.MatchedDTO;
import com.mpi.tools.api.dto.patient.PatientDTO;
import com.mpi.tools.api.model.MatchConfig;
import com.mpi.tools.api.model.MatchIssue;
import com.mpi.tools.api.model.MatchedRecord;
import com.mpi.tools.api.resource.interfaces.MatchIssueFeignClient;
import com.mpi.tools.api.resource.interfaces.MatchedPatientFeignClient;

import feign.FeignException.FeignClientException;

@EnableAsync
@Service
public class MatchedRecordService {

	@Autowired
	private MatchIssueService matchIssueService;

	@Autowired
	private MatchConfigService matchConfig;

	@Autowired
	private MatchedPatientFeignClient matchedPatientFeignClient;

	@Autowired
	private MatchIssueFeignClient matchIssueFeignClient;

	private final String UUID_CODE = "OpenMRS_PATIENT_UUID";

	private final String NID_CODE = "NID_TARV";

	private final String MATCH = "MATCH";

	private final String MALE = "male";

	private final String FEMALE = "female";

	private final String MALE_CONVERTED = "M";

	private final String FEMALE_CONVERTED = "F";

	public final Log logger = LogFactory.getLog(getClass());

	public String SaveMatchedInfo(MatchedDTO matchList, UserDTO user) {

		logger.info("User LOggado " + user.getUsername());

		for (PatientDTO patient : matchList.getEntry()) {
			this.createMatchedIssue(patient, user);
		}

		// Chamar o nextPage caso exista de forma recursiva
		for (LinkDTO nextPage : matchList.getLink()) {
			if (nextPage.getRelation().equals("next")) {
				if (!nextPage.getUrl().isEmpty()) {
					String nextURI = nextPage.getUrl().split("getpages=")[1].toString();

					try {
						// Resolver o bug
						MatchedDTO nextPageMatch = this.matchedPatientFeignClient.getPatientNextPage(nextURI);
						this.SaveMatchedInfo(nextPageMatch, user);
					} catch (FeignClientException e) {
						// this.saveNextPage(nextPage.getUrl());
						e.printStackTrace();
					}

				}
			}
		}

		return "done";
	}

	private void deleteMatchConfig() {

		this.matchConfig.deleteMatchConfig();
	}

	private void saveNextPage(String lastPage) {

		MatchConfig config = new MatchConfig();

		config.setType(MATCH);
		config.setActive(Boolean.TRUE);
		config.setLastPage(lastPage);
		config.setDateCreated(new Date());

		this.matchConfig.createMatchConfig(config);

	}

	// Formar os objectos
	private void createMatchedIssue(PatientDTO patient, UserDTO user) {

		// Antes de process

		MatchIssue matchIssue = new MatchIssue();
		matchIssue.setOpenmrsUuid(this.getUUID(patient.getResource().getIdentifier()));
		matchIssue.setOpenCrCruid(patient.getResource().getId());
		matchIssue.setDateCreated(new Date());
		// Sett Main match
		matchIssue.setBirthDate(patient.getResource().getBirthDate());
		matchIssue.setTarvNid(this.getNID(patient.getResource().getIdentifier()));
		matchIssue.setOpenmrsUuid(this.getUUID(patient.getResource().getIdentifier()));
		matchIssue.setDateCreated(new Date());
		matchIssue.setGender(this.convertGender(patient.getResource().getGender()));
		matchIssue.setProcessed(Boolean.TRUE);

		if (!patient.getResource().getName().isEmpty()) {
			if (!patient.getResource().getName().get(0).getGiven().isEmpty()) {
				matchIssue.setGivenName(patient.getResource().getName().get(0).getGiven().get(0));
			}

			matchIssue.setFamilyName(patient.getResource().getName().get(0).getFamily());

		}

		// Set main Match
		// sett Matched Patients
		matchIssue.setBirthDate(patient.getResource().getBirthDate());

		List<MatchIssueDTO> matchIssueDTOs = new ArrayList<>();

		try {

			matchIssueDTOs = this.getMatchedPatients(patient.getResource().getId(), user);

		} catch (Exception e) {

			// Create here unplied mathc patients
			// this.createUnapliedMatch(matchIssue);
			matchIssue.setProcessed(Boolean.FALSE);
			logger.info(
					"Mathced patient with id equal to " + matchIssue.getOpenCrCruid() + " not aplied successfully.");
			e.printStackTrace();

		}

		for (MatchIssueDTO matched : matchIssueDTOs) {

			logger.info("Matched Patient link - " + matched.getNid_tarv());
			logger.info("Matched patient id - " + patient.getResource().getId());

			if (this.hasUuid(matchIssue, matched)) {
				continue;
			}
			MatchedRecord matchedRecord = this.createMatchRecord(matched);
			matchedRecord.setMatchIssue(matchIssue);

			matchIssue.addMatcheRecords(matchedRecord);

		}

		this.matchIssueService.save(matchIssue);

	}

	private boolean hasUuid(MatchIssue matchIssue, MatchIssueDTO matched) {

		for (MatchedRecord matchedRecord : matchIssue.getMatchedRecords()) {
			if (matched.getUid().equals(matchedRecord.getOpenmrsUuid())) {
				return true;
			}
		}
		return false;
	}

	private List<MatchIssueDTO> getMatchedPatients(String patientId, UserDTO user) {

		List<MatchIssueDTO> matchIssueDTOs = new ArrayList<>();

		logger.info("Patient id " + patientId);

		matchIssueDTOs = this.matchIssueFeignClient.getMatchedPatients(patientId, "Basic ".concat(user.getToken()));
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

	private MatchedRecord createMatchRecord(MatchIssueDTO matchedPatient) {

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

	private String getUUID(List<IdentifierDTO> identifiers) {

		for (IdentifierDTO identifier : identifiers) {

			for (CodeDTO code : identifier.getType().getCoding()) {
				if (code.getCode().equals(UUID_CODE)) {
					logger.info("coder --- " + code.getCode() + " - " + identifier.getValue());
					return identifier.getValue();
				}
			}
		}
		return null;
	}

	private String getNID(List<IdentifierDTO> identifiers) {

		for (IdentifierDTO identifier : identifiers) {

			for (CodeDTO code : identifier.getType().getCoding()) {
				if (code.getCode().equals(NID_CODE)) {
					return identifier.getValue();
				}
			}
		}
		return null;
	}

	// This method should make a call to a Matched Patient
	// It should return all info a patient
	private PatientMatchedDTO findPatientMatched(String matchedReference) {

		PatientMatchedDTO patient = this.matchedPatientFeignClient.getPatientInfo(matchedReference);
		return patient;

	}

	// This method get the next page of duplicated patient
	private MatchedDTO getNextPage(String nextPageLink) {

		MatchedDTO nextMatchedPage = this.matchedPatientFeignClient.getPatientNextPage(nextPageLink);

		return nextMatchedPage;
	}

	public void saveUnapliedMatchInfo(List<MatchIssue> unapliedMatchInfos, UserDTO user) {

		int count = 0;

		for (MatchIssue unapliedMatchInfo : unapliedMatchInfos) {
			boolean hasError = Boolean.FALSE;
			// sett Matched Patients
			List<MatchIssueDTO> matchIssueDTOs = new ArrayList<>();

			try {

				matchIssueDTOs = this.getMatchedPatients(unapliedMatchInfo.getOpenCrCruid().toString(), user);

			} catch (Exception e) {

				// Create here unplied mathc patients
				// this.createUnapliedMatch(matchIssue);
				logger.info("Mathced patient with id equal to " + unapliedMatchInfo.getOpenCrCruid()
						+ " not aplied successfully.");
				hasError = Boolean.TRUE;
				e.printStackTrace();

			}

			for (MatchIssueDTO matched : matchIssueDTOs) {

				logger.info("Matched Patient link - " + matched.getNid_tarv());
				logger.info("Matched patient id - " + unapliedMatchInfo.getOpenCrCruid());

				MatchedRecord matchedRecord = this.createMatchRecord(matched);
				matchedRecord.setMatchIssue(unapliedMatchInfo);

				unapliedMatchInfo.addMatcheRecords(matchedRecord);

			}

			if (!hasError) {
				this.matchIssueService.save(unapliedMatchInfo);

			}

		}

	}
}
