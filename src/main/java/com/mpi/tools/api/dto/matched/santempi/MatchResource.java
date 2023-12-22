package com.mpi.tools.api.dto.matched.santempi;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mpi.tools.api.dto.matched.matched.issue.MatchIssue;
import com.mpi.tools.api.dto.matched.matched.issue.SanteMpiMatchIssueDTO;
import com.mpi.tools.api.resource.interfaces.MatchIssueFeignClient;
import com.mpi.tools.api.services.MatchIssueService;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchResource {
	
	private String holder;
	
	private String target;
	
	private List<MatchedResource> resource;
	
	public MatchResource() {
	}
	
	public String getHolder() {
		return holder;
	}
	
	public void setHolder(String holder) {
		this.holder = holder;
	}
	
	public String getTarget() {
		return target;
	}
	
	public void setTarget(String target) {
		this.target = target;
	}
	
	public List<MatchedResource> getResource() {
		return resource;
	}
	
	public void setResource(List<MatchedResource> resource) {
		this.resource = resource;
	}
	
	public List<? extends MatchIssue> parseToIssue(MatchIssueFeignClient client, MatchIssueService matchIssueService) {
		List<MatchIssue> matchIssueDTOs = new ArrayList<>();
		
		for (MatchedResource r : this.resource) {
			SanteMpiMatchIssueDTO issue = new SanteMpiMatchIssueDTO();
			issue.setId(r.getId());
			
			//First: try to load from resource
			try {
				issue.loadAdditionalData(r);
			}
			catch (Exception e) {}
			
			//If there is no data on the resource load from db
			if (issue.getUid() == null) {
				issue.loadAdditionalData(matchIssueService);
			}
			
			//If no data from db
			if (issue.getUid() == null) {
				issue.loadAdditionalData(client);
			}
			
			matchIssueDTOs.add(issue);
		}
		
		return matchIssueDTOs;
	}
	
}
