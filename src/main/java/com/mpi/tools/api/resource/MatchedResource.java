package com.mpi.tools.api.resource;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mpi.tools.api.config.UserData;
import com.mpi.tools.api.dto.matched.PatientMatchedDTO;
import com.mpi.tools.api.dto.matched.UserDTO;
import com.mpi.tools.api.dto.patient.MatchedDTO;
import com.mpi.tools.api.resource.interfaces.MatchedPatientFeignClient;
import com.mpi.tools.api.resource.interfaces.UserFeignClient;
import com.mpi.tools.api.services.MatchedRecordService;

@RestController
@RequestMapping("/matched-patient")
public class MatchedResource {

	@Autowired
	private MatchedPatientFeignClient matchedPatientFeignClient;

	@Autowired
	private MatchedRecordService matchedRecordService;

	@Autowired
	private UserFeignClient userFeignClient;

	@Autowired
	private UserData userData;

	@Autowired
	private HttpSession httpSession;

	private final String TOKEN = "TOKEN";

	@GetMapping
	public ResponseEntity<?> findAllMatched(UserDTO user) {

		MatchedDTO matches = this.matchedPatientFeignClient.getAllMatched();

		this.matchedRecordService.SaveMatchedInfo(matches, user);

		// Teste
		return matches != null ? ResponseEntity.ok("Patient Match created") : ResponseEntity.noContent().build();

	}

	@GetMapping("/cruid")
	public ResponseEntity<?> findPatientByCruid() {

		PatientMatchedDTO patientMatched = this.matchedPatientFeignClient
				.getPatientInfo("Patient/be9c0e64-6ac9-44aa-836d-b1b13a01452b");

		return patientMatched != null ? ResponseEntity.ok(patientMatched) : ResponseEntity.noContent().build();

	}

	@PostMapping("/authenticate")
	public ResponseEntity<?> authenticateUser() {

		// userData = new UserData();
		UserDTO dto = this.userFeignClient.authenticateUser(userData.getMpiUser(), userData.getPassword());
		System.out.println("userdata " + userData.getPassword().toString() + " - " + userData.getMpiUser());

		// httpSession.setAttribute(TOKEN, dto.getToken());

		System.out.print("PRINT USER SESSIONS " + dto.getToken());
		return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.noContent().build();

	}

}
