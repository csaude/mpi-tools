package com.mpi.tools.api.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

	private final String LOAD = "LOAD";

	public final Log logger = LogFactory.getLog(getClass());

	public String SaveMatchedInfo(MatchedDTO matchList, UserDTO user, MatchConfig config) {

		logger.info("============================= Apply Match Patient=========================================");

		String currentPage = "";

		if (config.getLastPage().equals("NO")
				|| (Integer.valueOf(config.getLastPage()) <= Integer.valueOf(this.getCustomNextPage(matchList)))) {

			for (PatientDTO patient : matchList.getEntry()) {

				this.createMatchedIssue(patient, user);

			}

		}

		// Chamar o nextPage caso exista de forma recursiva
		for (LinkDTO nextPage : matchList.getLink()) {
			if (nextPage.getRelation().equals("next")) {
				if (!nextPage.getUrl().isEmpty()) {
					String nextURI = nextPage.getUrl().split("getpages=")[1].toString();

					String actualCount = nextPage.getUrl().split("_count=")[1].toString();
					String countCount = actualCount.split("&")[0];
					currentPage = countCount;
					System.out.println(" current page " + currentPage);

					try {
						// Resolver o bug
						MatchedDTO nextPageMatch = this.matchedPatientFeignClient.getPatientNextPage(nextURI);
						this.SaveMatchedInfo(nextPageMatch, user, config);
						config.setLastPage(String.valueOf(0));
					} catch (FeignClientException e) {
						config.setLastPage(currentPage);
						this.saveNextPage(config);
						e.printStackTrace();
					}

				}
			}
		}

		logger.info("============================= Apply Match Patient=========================================");

		return "done";
	}

	private String getCustomNextPage(MatchedDTO matchList) {
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

	private void saveNextPage(MatchConfig config) {

		this.matchConfig.updateConfig(config);

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
		matchIssue.setProcessed(Boolean.FALSE);

		if (!patient.getResource().getName().isEmpty()) {
			if (!patient.getResource().getName().get(0).getGiven().isEmpty()) {
				matchIssue.setGivenName(patient.getResource().getName().get(0).getGiven().get(0));
			}

			matchIssue.setFamilyName(patient.getResource().getName().get(0).getFamily());

		}

		// Set main Match
		// sett Matched Patients
		matchIssue.setBirthDate(patient.getResource().getBirthDate());

		// List<MatchIssueDTO> matchIssueDTOs = new ArrayList<>();
		/*
		 * // DO IT LETTER try {
		 * 
		 * matchIssueDTOs = this.getMatchedPatients(patient.getResource().getId(),
		 * user);
		 * 
		 * for (MatchIssueDTO matched : matchIssueDTOs) {
		 * 
		 * logger.info("Matched Patient link - " + matched.getNid_tarv());
		 * logger.info("Matched patient id - " + patient.getResource().getId());
		 * 
		 * if (this.hasUuid(matchIssue, matched)) { continue; } MatchedRecord
		 * matchedRecord = this.createMatchRecord(matched);
		 * matchedRecord.setMatchIssue(matchIssue);
		 * 
		 * matchIssue.addMatcheRecords(matchedRecord);
		 * 
		 * }
		 * 
		 * } catch (Exception e) {
		 * 
		 * // Create here unplied mathc patients //
		 * this.createUnapliedMatch(matchIssue); matchIssue.setProcessed(Boolean.FALSE);
		 * logger.info( "Mathced patient with id equal to " +
		 * matchIssue.getOpenCrCruid() + " not applied successfully.");
		 * e.printStackTrace();
		 * 
		 * }
		 */

		// DOT IT LETTER
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

		logger.info("============================= Unplied Match =========================================");

		for (MatchIssue unapliedMatchInfo : unapliedMatchInfos) {
			boolean hasError = Boolean.FALSE;
			// sett Matched Patients
			List<MatchIssueDTO> matchIssueDTOs = new ArrayList<>();

			try {

				matchIssueDTOs = this.getMatchedPatients(unapliedMatchInfo.getOpenCrCruid().toString(), user);

				for (MatchIssueDTO matched : matchIssueDTOs) {

					if (this.hasUuid(unapliedMatchInfo, matched)) {
						continue;
					}

					logger.info("Matched Patient link - " + matched.getNid_tarv());
					logger.info("Matched patient id - " + unapliedMatchInfo.getOpenCrCruid());

					MatchedRecord matchedRecord = this.createMatchRecord(matched);
					matchedRecord.setMatchIssue(unapliedMatchInfo);

					unapliedMatchInfo.addMatcheRecords(matchedRecord);

				}

			} catch (Exception e) {

				// Create here unplied mathc patients
				logger.info("Mathced patient with id equal to " + unapliedMatchInfo.getOpenCrCruid()
						+ " not applied successfully.");
				hasError = Boolean.TRUE;
				e.printStackTrace();

			}

			if (!hasError) {
				unapliedMatchInfo.setProcessed(Boolean.TRUE);
				this.matchIssueService.save(unapliedMatchInfo);

			}

		}

		logger.info("============================= Unplied Match =========================================");

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
