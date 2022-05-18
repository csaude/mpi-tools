package com.mpi.tools.api.services;

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
import com.mpi.tools.api.dto.patient.PatientMatchDTO;
import com.mpi.tools.api.dto.patient.ResourceDTO;
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
		// MatchedRecord mainMatchedRecord = new MatchedRecord();
		matchIssue.setBirthDate(patient.getResource().getBirthDate());
		matchIssue.setTarvNid(this.getNID(patient.getResource().getIdentifier()));
		matchIssue.setOpenmrsUuid(this.getUUID(patient.getResource().getIdentifier()));
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

		List<MatchIssueDTO> matchIssueDTOs = this.getMatchedPatients(patient.getResource().getId(), user);

		for (MatchIssueDTO matched : matchIssueDTOs) {

			logger.info("Matched Patient link - " + matched.getNid_tarv());

			MatchedRecord matchedRecord = this.createMatchRecord(matched);
			matchedRecord.setMatchIssue(matchIssue);

			matchIssue.addMatcheRecords(matchedRecord);

		}

		this.matchIssueService.save(matchIssue);

	}

	private List<MatchIssueDTO> getMatchedPatients(String patientId, UserDTO user) {

		List<MatchIssueDTO> matchIssueDTOs;

		logger.info("Patient uuid " + patientId);

		matchIssueDTOs = this.matchIssueFeignClient.getMatchedPatients(patientId, "Basic ".concat(user.getToken()));

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
}
