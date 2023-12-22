package com.mpi.tools.api.dto.matched.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientHeaderDTO {

	private PatientDataDTO resource;

	private String fullUrl;

	public PatientHeaderDTO() {
	}
	
	public PatientHeaderDTO(PatientDataDTO resource, String fullUrl) {
		this.fullUrl = fullUrl;
		this.resource = resource;
	}
	
	public String getFullUrl() {
		return fullUrl;
	}

	public void setFullUrl(String fullUrl) {
		this.fullUrl = fullUrl;
	}

	public PatientDataDTO getResource() {
		return resource;
	}

	public void setResource(PatientDataDTO resource) {
		this.resource = resource;
	}

}
