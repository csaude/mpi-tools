package com.mpi.tools.api.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mpi.tools.api.dto.matched.PatientMatchedDTO;
import com.mpi.tools.api.dto.patient.MatchedDTO;
import com.mpi.tools.api.resource.interfaces.MatchedPatientFeignClient;
import com.mpi.tools.api.services.MatchedRecordService;

@RestController
@RequestMapping("/matched-patient")
public class MatchedResource {

	@Autowired
	private MatchedPatientFeignClient matchedPatientFeignClient;

	@Autowired
	private MatchedRecordService matchedRecordService;

	@GetMapping
	public ResponseEntity<?> findAllMatched() {

		MatchedDTO matches = this.matchedPatientFeignClient.getAllMatched();

		this.matchedRecordService.SaveMatchedInfo(matches);

		// Teste
		return matches != null ? ResponseEntity.ok("Patient Match created") : ResponseEntity.noContent().build();

	}

	@GetMapping("/cruid")
	public ResponseEntity<?> findPatientByCruid() {

		PatientMatchedDTO patientMatched = this.matchedPatientFeignClient
				.getPatientInfo("Patient/be9c0e64-6ac9-44aa-836d-b1b13a01452b");

		return patientMatched != null ? ResponseEntity.ok(patientMatched) : ResponseEntity.noContent().build();

	}

}
