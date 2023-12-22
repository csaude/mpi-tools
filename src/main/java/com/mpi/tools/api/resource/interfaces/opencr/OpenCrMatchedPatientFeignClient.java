package com.mpi.tools.api.resource.interfaces.opencr;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.mpi.tools.api.config.FeignClientConfig;
import com.mpi.tools.api.dto.matched.OpenCrMatchedDTO;
import com.mpi.tools.api.resource.interfaces.MatchedPatientFeignClient;

@FeignClient(name = "openCrMatchedPatientFeignClient", configuration = FeignClientConfig.class, url = "${mpi.fhir.host}")
public interface OpenCrMatchedPatientFeignClient extends MatchedPatientFeignClient {
	
	@GetMapping("/fhir/Patient/?_tag=http%3A%2F%2Fopenclientregistry.org%2Ffhir%2FmatchIssues%7CpotentialMatches%2Chttp%3A%2F%2Fopenclientregistry.org%2Ffhir%2FmatchIssues%7CconflictMatches&_count=${mpi.fhir.count.page}")
	OpenCrMatchedDTO getAllMatched(@PathVariable("offset") String offset);
	
	@GetMapping("fhir?_getpages={nexta}")
	OpenCrMatchedDTO getPatientNextPage(@PathVariable("nexta") String nexta);
	
}
