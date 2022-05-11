package com.mpi.tools.api.dto.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = {})
public class IdentifierDTO {

	private String id;

	private TypeDTO type;

	private String system;

	// UUID OR NID
	private String value;

	public TypeDTO getType() {
		return type;
	}

	public void setType(TypeDTO type) {
		this.type = type;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
