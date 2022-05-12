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
import com.mpi.tools.api.dto.patient.PatientMatchDTO;
import com.mpi.tools.api.dto.patient.ResourceDTO;
import com.mpi.tools.api.model.MatchIssue;
import com.mpi.tools.api.model.MatchedRecord;
import com.mpi.tools.api.resource.interfaces.MatchedPatientFeignClient;

import feign.FeignException.FeignClientException;

@Service
public class MatchedRecordService {

	@Autowired
	private MatchIssueService matchIssueService;

	@Autowired
	private MatchedPatientFeignClient matchedPatientFeignClient;

	private final String UUID_CODE = "OpenMRS_PATIENT_UUID";

	private final String NID_CODE = "NID_TARV";

	public final Log logger = LogFactory.getLog(getClass());

	public String SaveMatchedInfo(MatchedDTO matchList) {

		for (PatientDTO patient : matchList.getEntry()) {
			this.createMatchedIssue(patient);
		}

		// Chamar o nextPage caso exista de forma recursiva
		for (LinkDTO nextPage : matchList.getLink()) {
			if (nextPage.getRelation().equals("next")) {
				if (!nextPage.getUrl().isEmpty()) {
					// "http://160.242.33.26:58383/fhir?_getpages=9ff48d9c-a6d4-4700-b8f7-72c6a58079de&_getpagesoffset=100&_count=100&_pretty=true&_bundletype=searchset"
					logger.info("uri link" + nextPage.getUrl());
					String nextURI = nextPage.getUrl().split("getpages=")[1].toString();
					logger.info("uri link" + nextURI);

					try {
						// Resolver o bug
						MatchedDTO nextPageMatch = this.matchedPatientFeignClient.getPatientNextPage(nextURI);
						this.SaveMatchedInfo(nextPageMatch);
					} catch (FeignClientException e) {
						e.printStackTrace();
					}

				}
			}
		}

		return "done";
	}

	// Formar os objectos
	private void createMatchedIssue(PatientDTO patient) {

		MatchIssue matchIssue = new MatchIssue();
		matchIssue.setOpenmrsUuid(this.getUUID(patient.getResource().getIdentifier()));
		matchIssue.setOpenCrCruid(patient.getResource().getId());
		matchIssue.setDateCreated(new Date());

		// Sett Main match
		//MatchedRecord mainMatchedRecord = new MatchedRecord();
		matchIssue.setBirthDate(patient.getResource().getBirthDate());
		matchIssue.setTarvNid(this.getNID(patient.getResource().getIdentifier()));
		matchIssue.setOpenmrsUuid(this.getUUID(patient.getResource().getIdentifier()));
		matchIssue.setDateCreated(new Date());
		matchIssue.setGender(patient.getResource().getGender());

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
