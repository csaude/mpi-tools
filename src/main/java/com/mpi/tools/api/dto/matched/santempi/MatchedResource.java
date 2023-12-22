package com.mpi.tools.api.dto.matched.santempi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mpi.tools.api.dto.matched.patient.IdentifierDTO;
import com.mpi.tools.api.dto.matched.patient.NameComponentDTO;
import com.mpi.tools.api.dto.matched.patient.NameDTO;
import com.mpi.tools.api.services.MatchedRecordService;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchedResource {
	
	private String id;
	
	private String genderConcept;
	
	private String dateOfBirth;
	
	private List<IdentifierDTO> identifier;
	
	private List<NameDTO> name;
	
	public String getGenderConcept() {
		return genderConcept;
	}
	
	public void setGenderConcept(String genderConcept) {
		this.genderConcept = genderConcept;
	}
	
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	public List<IdentifierDTO> getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(List<IdentifierDTO> identifier) {
		this.identifier = identifier;
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

	public String extractFamilyName() {
		for (NameComponentDTO nc: name.get(0).getComponent()) {
			if (nc.getType().equals("29b98455-ed61-49f8-a161-2d73363e1df0")) {
				return nc.getValue();
			}
		}
		
		throw new RuntimeException("No family name found");
	}
	
	public String extractGiveName() {
		for (NameComponentDTO nc: name.get(0).getComponent()) {
			if (nc.getType().equals("2f64bde2-a696-4b0a-9690-b21ebd7e5092")) {
				return nc.getValue();
			}
		}
		
		throw new RuntimeException("No given name found");
	}
	
	public String extractGender() {
		if (genderConcept.equals("f4e3a6bb-612e-46b2-9f77-ff844d971198")) {
			return MatchedRecordService.MALE;
		}
		else
		if (genderConcept.equals("094941e9-a3db-48b5-862c-bc289bd7f86c")) {
			return MatchedRecordService.FEMALE;
		}
		
		throw new RuntimeException("Uknown gender concept");
	}

	public String extractNidTarv() {
		return findIdentifier("NID_TARV");
	}
	
	public String getOpenMrsUuid() {
		return findIdentifier("PATIENT_UUID");
	}
	
	private String findIdentifier(String name) {
		for (IdentifierDTO i : this.identifier) {
			if (i.getAuthority().getDomainName().equals(name)) {
				return i.getValue();
			}
		}
		
		return null;
	}
}
