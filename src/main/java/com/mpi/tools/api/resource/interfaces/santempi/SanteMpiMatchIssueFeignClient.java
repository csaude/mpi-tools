package com.mpi.tools.api.resource.interfaces.santempi;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.mpi.tools.api.config.FeignClientConfig;
import com.mpi.tools.api.dto.matched.patient.PatientDataDTO;
import com.mpi.tools.api.dto.matched.santempi.MatchResource;
import com.mpi.tools.api.resource.interfaces.MatchIssueFeignClient;

import feign.Headers;

@FeignClient(name = "santeMpiMatchedIssueFeignClient", configuration = FeignClientConfig.class, url = "${mpi.fhir.host}")
public interface SanteMpiMatchIssueFeignClient extends MatchIssueFeignClient{

	@GetMapping("/hdsi/Patient/{id}/mdm-candidate")
	MatchResource getMatchedPatients(@PathVariable("id") String id);

	@Headers({
		"Accept", "application/fhir+json"
	})
	@GetMapping("/fhir/Patient/{id}")
	PatientDataDTO getPatient(@PathVariable("id") String id);
}
