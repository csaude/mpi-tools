package com.mpi.tools.api.resource.interfaces.santempi;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mpi.tools.api.config.FeignClientConfig;
import com.mpi.tools.api.dto.matched.SanteMpiMatchedDTO;
import com.mpi.tools.api.resource.interfaces.MatchedPatientFeignClient;

import feign.Headers;

@FeignClient(name = "santeMpiMatchedPatientFeignClient", configuration = FeignClientConfig.class, url = "${mpi.fhir.host}")
public interface SanteMpiMatchedPatientFeignClient extends MatchedPatientFeignClient{


	@Headers("Accept: application/json")
	@GetMapping("/hdsi/Patient/mdm-candidate?_count=100")
	SanteMpiMatchedDTO getAllMatched(@RequestParam("_offset") String _offset);
	
	@Headers("Accept: application/json")
	@GetMapping("/hdsi/Patient/mdm-candidate")
	SanteMpiMatchedDTO getPatientNextPage(@RequestParam("_offset") String _offset);
	

}
