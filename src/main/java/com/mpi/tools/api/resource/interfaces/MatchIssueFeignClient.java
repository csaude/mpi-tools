package com.mpi.tools.api.resource.interfaces;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.mpi.tools.api.config.FeignClientConfig;
import com.mpi.tools.api.dto.matched.MatchIssueDTO;

@FeignClient(name = "matchedIssueFeignClient", configuration = FeignClientConfig.class, url = "${mpi.ocrux.host}/ocrux")
public interface MatchIssueFeignClient {

	@GetMapping("/match/potential-matches/{id}")
	public List<MatchIssueDTO> getMatchedPatients(@PathVariable("id") String id,
			@RequestHeader(value = "Authorization", required = true) String authorizationHeader);

}
