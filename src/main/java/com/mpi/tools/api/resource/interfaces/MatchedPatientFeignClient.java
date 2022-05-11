package com.mpi.tools.api.resource.interfaces;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.mpi.tools.api.config.FeignClientConfig;
import com.mpi.tools.api.dto.matched.PatientMatchedDTO;
import com.mpi.tools.api.dto.patient.MatchedDTO;

@FeignClient(name = "matchedPatientFeignClient", configuration = FeignClientConfig.class, url = "http://160.242.33.26:58383")
public interface MatchedPatientFeignClient {

	@GetMapping("/fhir/Patient/?_tag=http%3A%2F%2Fopenclientregistry.org%2Ffhir%2FmatchIssues%7CpotentialMatches%2Chttp%3A%2F%2Fopenclientregistry.org%2Ffhir%2FmatchIssues%7CconflictMatches&_count=1")
	MatchedDTO getAllMatched();

	@GetMapping("/fhir/{cruid}/$everything")
	PatientMatchedDTO getPatientInfo(@PathVariable("cruid") String cruid);
	
	
	@GetMapping("fhir?_getpages={nexta}")
	MatchedDTO getPatientNextPage(@PathVariable("nexta") String nexta);

}
