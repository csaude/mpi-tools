package com.mpi.tools.api.dto.matched.matched.issue;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mpi.tools.api.dto.matched.patient.LinkDTO;
import com.mpi.tools.api.dto.matched.patient.PatientHeaderDTO;

/**
 * 
 * Representa os client que encontrados como possiveis matches
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientMatchedDTO {

    @JsonProperty("entry")
	private List<PatientHeaderDTO> entry = new ArrayList<PatientHeaderDTO>();

	private String id;

	private String resourceType;

	private List<LinkDTO> link;

	public List<PatientHeaderDTO> getEntry() {
		return entry;
	}

	public void setEntry(List<PatientHeaderDTO> entry) {
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
