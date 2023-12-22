package com.mpi.tools.api.dto.matched.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IdentifierDTO {
	
	private String id;
	
	private TypeDTO type;
	
	private String system;
	
	// UUID OR NID
	private String value;
	
	private AutorityDTO authority;
	
	public AutorityDTO getAuthority() {
		return authority;
	}
	
	public void setAuthority(AutorityDTO authority) {
		this.authority = authority;
	}
	
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
