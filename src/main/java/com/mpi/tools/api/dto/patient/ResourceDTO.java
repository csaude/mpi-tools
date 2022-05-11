package com.mpi.tools.api.dto.patient;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = { "lastUpdate", "source", "meta", "text", "address", "deceasedDateTime", "telecom",
		"extension", "contact", "type", "subtype", "action", "recorded", "outcome" ,"outcomeDesc", "agent", "entity"})
public class ResourceDTO {

	private String id;

	private String resourceType;

	@JsonProperty("identifier")
	private List<IdentifierDTO> identifier = new ArrayList<>();

	@JsonProperty("active")
	private boolean active;

	private List<NameDTO> name = new ArrayList<>();

	private String gender;

	private String birthDate;

	private List<PatientMatchDTO> link;

	public List<IdentifierDTO> getIdentifier() {
		return identifier;
	}

	public void setIdentifier(List<IdentifierDTO> identifier) {
		this.identifier = identifier;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<NameDTO> getName() {
		return name;
	}

	public void setName(List<NameDTO> name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public List<PatientMatchDTO> getLink() {
		return link;
	}

	public void setLink(List<PatientMatchDTO> link) {
		this.link = link;
	}

}
