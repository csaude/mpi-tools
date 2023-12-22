package com.mpi.tools.api.dto.matched.matched.issue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mpi.tools.api.dto.matched.patient.PatientDataDTO;
import com.mpi.tools.api.dto.matched.santempi.MatchedResource;
import com.mpi.tools.api.resource.interfaces.MatchIssueFeignClient;
import com.mpi.tools.api.resource.interfaces.santempi.SanteMpiMatchIssueFeignClient;
import com.mpi.tools.api.services.MatchIssueService;
import com.mpi.tools.api.services.MatchedRecordService;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SanteMpiMatchIssueDTO implements MatchIssue {
	
	private String id;
	
	private String family;
	
	private String gender;
	
	private String given;
	
	private String birthDate;
	
	private String nid_tarv;
	
	private String uid;
	
	private String ouid;
	
	private String source_id;
	
	private String source;
	
	private Object scores;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public String getGiven() {
		return given;
	}
	
	public void setGiven(String given) {
		this.given = given;
	}
	
	public String getBirthDate() {
		return birthDate;
	}
	
	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}
	
	public String getNid_tarv() {
		return nid_tarv;
	}
	
	public void setNid_tarv(String nid_tarv) {
		this.nid_tarv = nid_tarv;
	}
	
	public String getUid() {
		return uid;
	}
	
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	public String getOuid() {
		return ouid;
	}
	
	public void setOuid(String ouid) {
		this.ouid = ouid;
	}
	
	public String getSource_id() {
		return source_id;
	}
	
	public void setSource_id(String source_id) {
		this.source_id = source_id;
	}
	
	public String getSource() {
		return source;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public Object getScores() {
		return scores;
	}
	
	public void setScores(Object scores) {
		this.scores = scores;
	}
	
	public String getFamily() {
		return family;
	}
	
	public void setFamily(String family) {
		this.family = family;
	}
	
	@Override
	public void loadAdditionalData(MatchedResource matchedResource) {
		this.birthDate = matchedResource.getDateOfBirth();
		this.family = matchedResource.extractFamilyName();
		this.gender = matchedResource.extractGender();
		this.given = matchedResource.extractGiveName();
		this.nid_tarv = matchedResource.extractNidTarv();
		this.uid = matchedResource.getOpenMrsUuid();
		this.ouid = matchedResource.getId();
	}
	
	@Override
	public void loadAdditionalData(MatchIssueFeignClient client) {
		copy(((SanteMpiMatchIssueFeignClient) client).getPatient(id));
	}
	
	@Override
	public void loadAdditionalData(MatchIssueService matchIssueService) {
		com.mpi.tools.api.model.MatchIssue patient = matchIssueService.getByMatchedRecordMpiId(getId());
		
		if (patient != null) {
			this.birthDate = patient.getBirthDate();
			this.family = patient.getFamilyName();
			this.gender = patient.getGender().equals(MatchedRecordService.FEMALE) ? MatchedRecordService.FEMALE_CONVERTED : MatchedRecordService.MALE;
			this.given = patient.getGivenName();
			this.nid_tarv = patient.getTarvNid();
			this.uid = patient.getOpenmrsUuid();
			this.ouid = patient.getOpenCrCruid();
		}
	}
	
	public void copy(PatientDataDTO patient) {
		this.birthDate = patient.getBirthDate();
		this.family = patient.getName().get(0).getFamily();
		this.gender = patient.getGender();
		this.given = String.join(" ", patient.getName().get(0).getGiven());
		this.nid_tarv = MatchedRecordService.getNID(patient.getIdentifier());
		this.uid = MatchedRecordService.getUUID(patient.getIdentifier());
		this.ouid = patient.getId();
	}
	
	@Override
	public String toString() {
		return "SanteMpiMatchIssueDTO [id=" + id + ", family=" + family + ", gender=" + gender + ", given=" + given
		        + ", birthDate=" + birthDate + ", nid_tarv=" + nid_tarv + ", uid=" + uid + ", ouid=" + ouid + ", source_id="
		        + source_id + ", source=" + source + ", scores=" + scores + "]";
	}
}
