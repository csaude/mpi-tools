package com.mpi.tools.api.dto.matched.patient;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NameDTO {
	
	private String id;
	
	private String use;
	
	private String family;
	
	private List<String> given = new ArrayList<>();
	
	private List<NameComponentDTO> component;
	
	public List<NameComponentDTO> getComponent() {
		return component;
	}
	
	public void setComponent(List<NameComponentDTO> component) {
		this.component = component;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getUse() {
		return use;
	}
	
	public void setUse(String use) {
		this.use = use;
	}
	
	public String getFamily() {
		return family;
	}
	
	public void setFamily(String family) {
		this.family = family;
	}
	
	public List<String> getGiven() {
		return given;
	}
	
	public void setGiven(List<String> given) {
		this.given = given;
	}
	
}
