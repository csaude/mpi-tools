package com.mpi.tools.api.dto.patient;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchedDTO {

	@JsonProperty("entry")
	private List<PatientDTO> entry = new ArrayList<PatientDTO>();

	private String id;

	private String resourceType;

	private List<LinkDTO> link;

	public List<PatientDTO> getEntry() {
		return entry;
	}

	public void setEntry(List<PatientDTO> entry) {
		this.entry = entry;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<LinkDTO> getLink() {
		return link;
	}

	public void setLink(List<LinkDTO> link) {
		this.link = link;
	}

}
