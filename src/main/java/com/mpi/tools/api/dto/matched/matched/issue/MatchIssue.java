package com.mpi.tools.api.dto.matched.matched.issue;

import com.mpi.tools.api.dto.matched.santempi.MatchedResource;
import com.mpi.tools.api.resource.interfaces.MatchIssueFeignClient;
import com.mpi.tools.api.services.MatchIssueService;

public interface MatchIssue {
	
	public String getId();
	
	public void setId(String id);
	
	public String getGender();
	
	public void setGender(String gender);
	
	public String getGiven();
	
	public void setGiven(String given);
	
	public String getBirthDate();
	
	public void setBirthDate(String birthDate);
	
	public String getNid_tarv();
	
	public void setNid_tarv(String nid_tarv);
	
	public String getUid();
	
	public void setUid(String uid);
	
	public String getOuid();
	
	public void setOuid(String ouid);
	
	public String getSource_id();
	
	public void setSource_id(String source_id);
	
	public String getSource();
	
	public void setSource(String source);
	
	public Object getScores();
	
	public void setScores(Object scores);
	
	public String getFamily();
	
	public void setFamily(String family);
	
	public void loadAdditionalData(MatchIssueFeignClient client);
	
	public void loadAdditionalData(MatchIssueService matchIssueService);
		
	public void loadAdditionalData(MatchedResource matchIssueService) ;
}
