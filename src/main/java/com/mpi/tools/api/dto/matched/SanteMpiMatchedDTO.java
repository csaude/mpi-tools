package com.mpi.tools.api.dto.matched;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mpi.tools.api.dto.matched.patient.LinkDTO;
import com.mpi.tools.api.dto.matched.patient.PatientHeaderDTO;
import com.mpi.tools.api.dto.matched.santempi.MatchResource;
import com.mpi.tools.api.model.MatchConfig;
import com.mpi.tools.api.resource.interfaces.MatchIssueFeignClient;
import com.mpi.tools.api.resource.interfaces.MatchedPatientFeignClient;
import com.mpi.tools.api.resource.interfaces.santempi.SanteMpiMatchIssueFeignClient;
import com.mpi.tools.api.resource.interfaces.santempi.SanteMpiMatchedPatientFeignClient;
import com.mpi.tools.api.services.MatchIssueService;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SanteMpiMatchedDTO implements Matched {
	
	@JsonProperty("entry")
	private List<PatientHeaderDTO> entry = new ArrayList<PatientHeaderDTO>();
	
	private String id;
	
	private String resourceType;
	
	private List<LinkDTO> link;
	
	private List<MatchResource> resource;
	
	public List<MatchResource> getResource() {
		return resource;
	}
	
	public void setResource(List<MatchResource> resource) {
		this.resource = resource;
	}
	
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
	
	@Override
	public void doFullLoad(SanteMpiMatchIssueFeignClient client, MatchIssueService matchIssueService) {
		entry = new ArrayList<>();
		
		for (MatchResource resource : this.resource) {
			if (matchIssueService.getByMatchedRecordMpiId(resource.getHolder()) == null) {
				entry.add(new PatientHeaderDTO(client.getPatient(resource.getHolder()), ""));
			}
			
			if (matchIssueService.getByMatchedRecordMpiId(resource.getTarget()) == null) {
				entry.add(new PatientHeaderDTO(client.getPatient(resource.getTarget()), ""));
			}
		}
	}
	
	@Override
	public boolean hasNextPage() {
		return true;
	}
	
	@Override
	public Matched goNextPage(MatchedPatientFeignClient client, MatchIssueFeignClient matchIssueClient, MatchConfig config, MatchIssueService matchIssueService) {
		Matched matched = ((SanteMpiMatchedPatientFeignClient) client).getPatientNextPage(config.getLastPage());
		
		matched.doFullLoad((SanteMpiMatchIssueFeignClient) matchIssueClient, matchIssueService);
		
		return matched;
	}
}
