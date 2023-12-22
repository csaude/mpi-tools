package com.mpi.tools.api.dto.matched.matched.issue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mpi.tools.api.dto.matched.santempi.MatchedResource;
import com.mpi.tools.api.resource.interfaces.MatchIssueFeignClient;
import com.mpi.tools.api.services.MatchIssueService;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenCrMatchIssueDTO implements MatchIssue {
	
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
	public void loadAdditionalData(MatchIssueFeignClient client) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void loadAdditionalData(MatchIssueService matchIssueService) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadAdditionalData(MatchedResource matchIssueService) {
		// TODO Auto-generated method stub
		
	}
	
}
