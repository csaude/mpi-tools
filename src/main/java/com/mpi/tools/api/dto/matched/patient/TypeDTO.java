package com.mpi.tools.api.dto.matched.patient;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = {})
public class TypeDTO {

 	private List<CodeDTO> coding = new ArrayList<>();
 	
 	private String text;

	public List<CodeDTO> getCoding() {
		return coding;
	}

	public void setCoding(List<CodeDTO> coding) {
		this.coding = coding;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	

}
