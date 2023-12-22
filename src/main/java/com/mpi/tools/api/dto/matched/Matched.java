package com.mpi.tools.api.dto.matched;

import java.util.List;

import com.mpi.tools.api.dto.matched.patient.LinkDTO;
import com.mpi.tools.api.dto.matched.patient.PatientHeaderDTO;
import com.mpi.tools.api.model.MatchConfig;
import com.mpi.tools.api.resource.interfaces.MatchIssueFeignClient;
import com.mpi.tools.api.resource.interfaces.MatchedPatientFeignClient;
import com.mpi.tools.api.resource.interfaces.santempi.SanteMpiMatchIssueFeignClient;
import com.mpi.tools.api.services.MatchIssueService;

/**
 * 
 * Representa os client que encontrados como possiveis matches
 */
public interface Matched {

	public List<PatientHeaderDTO> getEntry() ;

	public void setEntry(List<PatientHeaderDTO> entry) ;

	public String getResourceType();

	public void setResourceType(String resourceType);

	public String getId();

	public void setId(String id);

	public List<LinkDTO> getLink();

	public void setLink(List<LinkDTO> link);
	
	public void doFullLoad(SanteMpiMatchIssueFeignClient client, MatchIssueService matchIssueService);

	public boolean hasNextPage();

	public Matched goNextPage(MatchedPatientFeignClient client, MatchIssueFeignClient matchIssueClient, MatchConfig config, MatchIssueService matchIssueService);
}
