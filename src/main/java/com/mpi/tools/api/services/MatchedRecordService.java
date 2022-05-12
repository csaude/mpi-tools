package com.mpi.tools.api.services;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mpi.tools.api.dto.matched.PatientMatchedDTO;
import com.mpi.tools.api.dto.patient.CodeDTO;
import com.mpi.tools.api.dto.patient.IdentifierDTO;
import com.mpi.tools.api.dto.patient.LinkDTO;
import com.mpi.tools.api.dto.patient.MatchedDTO;
import com.mpi.tools.api.dto.patient.PatientDTO;
import com.mpi.tools.api.dto.patient.ResourceDTO;
import com.mpi.tools.api.model.MatchConfig;
import com.mpi.tools.api.model.MatchIssue;
import com.mpi.tools.api.model.MatchedRecord;
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

	private final String UUID_CODE = "OpenMRS_PATIENT_UUID";

	private final String NID_CODE = "NID_TARV";

	private final String MATCH = "MATCH";

	private final String MALE = "male";

	private final String FEMALE = "female";

	private final String MALE_CONVERTED = "Masculino";

	private final String FEMALE_CONVERTED = "Femenino";

	public final Log logger = LogFactory.getLog(getClass());

	public String SaveMatchedInfo(MatchedDTO matchList) {

		// verificar a ultima pagina corrida
		List<MatchConfig> config = this.matchConfig.findMatchConfig(MATCH);

		if (config != null && !config.isEmpty()) {
			String nextURI = config.get(config.size() - 1).getLastPage().split("getpages=")[1].toString();
			MatchedDTO nextPageMatch = this.matchedPatientFeignClient.getPatientNextPage(nextURI);
			this.deleteMatchConfig();

			this.SaveMatchedInfo(nextPageMatch);
			logger.info("Match Found");

		}

		for (PatientDTO patient : matchList.getEntry()) {
			this.createMatchedIssue(patient);
		}

		// Chamar o nextPage caso exista de forma recursiva
		for (LinkDTO nextPage : matchList.getLink()) {
			if (nextPage.getRelation().equals("next")) {
				if (!nextPage.getUrl().isEmpty()) {
					String nextURI = nextPage.getUrl().split("getpages=")[1].toString();

					try {
						// Resolver o bug
						MatchedDTO nextPageMatch = this.matchedPatientFeignClient.getPatientNextPage(nextURI);
						this.SaveMatchedInfo(nextPageMatch);
					} catch (FeignClientException e) {
						this.saveNextPage(nextPage.getUrl());
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
	private void createMatchedIssue(PatientDTO patient) {

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

		/*
		 * // mainMatchedRecord.setMatchIssue(matchIssue);
		 * 
		 * // matchIssue.addMatcheRecords(mainMatchedRecord);
		 * 
		 * // Set main Match
		 * 
		 * // sett Matched for (PatientMatchDTO matched :
		 * patient.getResource().getLink()) { if (matched.getOther() != null) {
		 * 
		 * logger.info("Matched Patient link - " + matched.getOther().getReference());
		 * PatientMatchedDTO matchedPatient =
		 * this.findPatientMatched(matched.getOther().getReference());
		 * 
		 * MatchedRecord matchedRecord = this.createMatchRecord(matchedPatient);
		 * matchedRecord.setMatchIssue(matchIssue);
		 * 
		 * matchIssue.addMatcheRecords(matchedRecord); } }
		 */

		this.matchIssueService.save(matchIssue);

	}

	private String convertGender(String gender) {

		if (gender != null && gender.equals(FEMALE)) {

			return FEMALE_CONVERTED;
		} else if (gender != null && gender.equals(MALE)) {

			return MALE_CONVERTED;
		}
		return null;
	}

	private MatchedRecord createMatchRecord(PatientMatchedDTO matchedPatient) {

		MatchedRecord matchedRecord = new MatchedRecord();

		ResourceDTO patientResourceInfo = this.findPatientResourceDTO(matchedPatient.getEntry());
		matchedRecord.setBirthDate(patientResourceInfo.getBirthDate());
		matchedRecord.setOpencr_cruid(patientResourceInfo.getId());
		matchedRecord.setTarvNid(this.getNID(patientResourceInfo.getIdentifier()));
		matchedRecord.setOpenmrsUuid(this.getUUID(patientResourceInfo.getIdentifier()));
		matchedRecord.setDateCreated(new Date());
		matchedRecord.setGender(patientResourceInfo.getGender());

		if (!patientResourceInfo.getName().isEmpty()) {
			if (!patientResourceInfo.getName().get(0).getGiven().isEmpty()) {
				matchedRecord.setGivenName(patientResourceInfo.getName().get(0).getGiven().get(0));
			}

			matchedRecord.setFamilyName(patientResourceInfo.getName().get(0).getFamily());

		}

		return matchedRecord;
	}

	private ResourceDTO findPatientResourceDTO(List<PatientDTO> entry) {

		for (PatientDTO patientDTO : entry) {
			if (!patientDTO.getResource().getIdentifier().isEmpty()) {
				return patientDTO.getResource();
			}
		}
		return null;
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
