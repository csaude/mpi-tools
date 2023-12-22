package com.mpi.tools.api.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mpi.tools.api.config.UserData;
import com.mpi.tools.api.dto.matched.Matched;
import com.mpi.tools.api.dto.matched.matched.issue.UserDTO;
import com.mpi.tools.api.model.MatchConfig;
import com.mpi.tools.api.model.MatchIssue;
import com.mpi.tools.api.resource.interfaces.Authenticator;
import com.mpi.tools.api.resource.interfaces.MatchedPatientFeignClient;
import com.mpi.tools.api.resource.interfaces.opencr.OpenCrMatchIssueFeignClient;
import com.mpi.tools.api.resource.interfaces.opencr.OpenCrMatchedPatientFeignClient;
import com.mpi.tools.api.resource.interfaces.santempi.SanteMpiMatchIssueFeignClient;
import com.mpi.tools.api.resource.interfaces.santempi.SanteMpiMatchedPatientFeignClient;
import com.mpi.tools.api.services.MatchConfigService;
import com.mpi.tools.api.services.MatchIssueService;
import com.mpi.tools.api.services.MatchedRecordService;
import com.mpi.tools.api.utils.OpenCrAuthenticator;
import com.mpi.tools.api.utils.SanteMpiAuthenticator;

import feign.FeignException.FeignClientException;

@RestController
public class MatchedResource {
	
	@Autowired
	private SanteMpiMatchedPatientFeignClient santeMpimatchedPatientFeignClient;
	
	@Autowired
	private OpenCrMatchedPatientFeignClient openCrmatchedPatientFeignClient;
	
	@Autowired
	private SanteMpiMatchIssueFeignClient santeMpimatchIssueFeignClient;
	
	@Autowired
	private OpenCrMatchIssueFeignClient openCrmatchIssueFeignClient;
	
	@Autowired
	private MatchedRecordService matchedRecordService;
	
	@Autowired
	private OpenCrAuthenticator openCruserAuthenticator;
	
	@Autowired
	private SanteMpiAuthenticator santeMpiAuthenticator;
	
	@Autowired
	private UserData userData;
	
	@Autowired
	private MatchConfigService matchConfigService;
	
	@Autowired
	private MatchIssueService matchIssueService;
	
	private String system;
	
	public MatchedResource() {
		this.system = "santempi";
	}
	
	public MatchedResource(String system) {
		this.system = system;
	}
	
	@GetMapping
	public ResponseEntity<?> findAllMatched(UserDTO user) {
		
		MatchConfig config = this.matchedRecordService.openLoadPage();
		
		if (config != null && config.isActive()) {
			Matched matched = this.getMatchedPatientClient().getAllMatched(String.valueOf(1));
			
			matched.doFullLoad(this.getMatchIssueClient(), matchIssueService);
			
			this.matchedRecordService.saveMatchedInfo(matched, config, getMatchedPatientClient(), getMatchIssueClient());
			
			// Chamar o nextPage caso exista de forma recursiva
			
			while (matched.hasNextPage()) {
				try {
					int page = Integer.parseInt(config.getLastPage()) + matched.getEntry().size() - 1;
					
					config.setLastPage("" + page);
					
					matched = matched.goNextPage(getMatchedPatientClient(), getMatchIssueClient(), config,
					    matchIssueService);
					
					this.saveNextPage(config);
					
					matchedRecordService.saveMatchedInfo(matched, config, getMatchedPatientClient(), getMatchIssueClient());
				}
				catch (FeignClientException e) {
					e.printStackTrace();
				}
				finally {
					this.saveNextPage(config);
				}
			}
			
		}
		
		return ResponseEntity.ok("Patient Match created");
		
	}
	
	private void saveNextPage(MatchConfig config) {
		matchConfigService.updateConfig(config);
	}
	
	//@Scheduled(initialDelay = 3600000, fixedRate = 3600000)
	public void resolveUnapliedMatch() {
		//int pageNo = 1;
		int pageSize = 100;
		
		//PageRequest pageRequest = PageRequest.of(pageNo - 1, pageSize);
		
		List<MatchIssue> unapliedMatchInfos = this.matchIssueService.findFirstN_NotProcessedWithNoEmptyName(pageSize);
		
		while (unapliedMatchInfos != null && unapliedMatchInfos.size() > 0) {
			this.matchedRecordService.saveUnapliedMatchInfo(unapliedMatchInfos, getMatchIssueClient());
			
			//pageRequest = PageRequest.of(++pageNo - 1, pageSize);
			
			unapliedMatchInfos = this.matchIssueService.findFirstN_NotProcessedWithNoEmptyName(pageSize);
		}
		
		//return unapliedMatchInfos != null ? ResponseEntity.ok(unapliedMatchInfos) : ResponseEntity.noContent().build();
		
	}
	
	public UserDTO authenticate() {
		return getAutenticator().autenticate(userData);
	}
	
	public SanteMpiMatchedPatientFeignClient getMatchedPatientClient() {
		if (system.equals("santempi")) {
			return santeMpimatchedPatientFeignClient;
		} else if (system.equals("opencr")) {
			return null;//openCrmatchedPatientFeignClient;
		} else
			throw new RuntimeException("The specified system is unsupported. Should use ['santempi', 'opencr'] ");
	}
	
	public SanteMpiMatchIssueFeignClient getMatchIssueClient() {
		if (system.equals("santempi")) {
			return santeMpimatchIssueFeignClient;
		} else if (system.equals("opencr")) {
			return null;//openCrmatchIssueFeignClient;
		} else
			throw new RuntimeException("The specified system is unsupported. Should use ['santempi', 'opencr'] ");
	}
	
	public MatchedPatientFeignClient getAutenticatorClient() {
		if (system.equals("santempi")) {
			return santeMpimatchedPatientFeignClient;
		} else if (system.equals("opencr")) {
			return openCrmatchedPatientFeignClient;
		} else
			throw new RuntimeException("The specified system is unsupported. Should use ['santempi', 'opencr'] ");
	}
	
	public Authenticator getAutenticator() {
		if (system.equals("santempi")) {
			return santeMpiAuthenticator;
		} else if (system.equals("opencr")) {
			return openCruserAuthenticator;
		} else
			throw new RuntimeException("The specified system is unsupported. Should use ['santempi', 'opencr'] ");
	}
	
}
