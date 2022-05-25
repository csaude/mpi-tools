package com.mpi.tools.api.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mpi.tools.api.config.UserData;
import com.mpi.tools.api.dto.matched.PatientMatchedDTO;
import com.mpi.tools.api.dto.matched.UserDTO;
import com.mpi.tools.api.dto.patient.MatchedDTO;
import com.mpi.tools.api.model.MatchConfig;
import com.mpi.tools.api.model.MatchIssue;
import com.mpi.tools.api.resource.interfaces.MatchedPatientFeignClient;
import com.mpi.tools.api.resource.interfaces.UserFeignClient;
import com.mpi.tools.api.services.MatchIssueService;
import com.mpi.tools.api.services.MatchedRecordService;

@RestController
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
	private MatchIssueService matchIssueService;

	@GetMapping
	public ResponseEntity<?> findAllMatched(UserDTO user) {

		MatchConfig config = this.matchedRecordService.openLoadPage();

		if (config != null && config.isActive()) {
			MatchedDTO matches = this.matchedPatientFeignClient.getAllMatched();

			this.matchedRecordService.SaveMatchedInfo(matches, user, config);
		}

		this.matchedRecordService.closeLoadPage(config);

		// Teste
		return ResponseEntity.ok("Patient Match created");

	}

	public ResponseEntity<?> findPatientByCruid() {

		PatientMatchedDTO patientMatched = this.matchedPatientFeignClient
				.getPatientInfo("Patient/be9c0e64-6ac9-44aa-836d-b1b13a01452b");

		return patientMatched != null ? ResponseEntity.ok(patientMatched) : ResponseEntity.noContent().build();

	}

	public ResponseEntity<?> authenticateUser() {

		// userData = new UserData();
		UserDTO dto = this.userFeignClient.authenticateUser(userData.getMpiUser(), userData.getPassword());
		System.out.println("userdata " + userData.getPassword().toString() + " - " + userData.getMpiUser());

		System.out.print("PRINT USER SESSIONS " + dto.getToken());
		return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.noContent().build();

	}

	//@Scheduled(initialDelay = 3600000, fixedRate = 3600000)
	public ResponseEntity<?> resolveUnapliedMatch() {

		@SuppressWarnings("unchecked")
		ResponseEntity<UserDTO> user = (ResponseEntity<UserDTO>) this.authenticateUser();

		List<MatchIssue> unapliedMatchInfos = this.matchIssueService.findAllNotProcessedWithNoEmptyName();

		this.matchedRecordService.saveUnapliedMatchInfo(unapliedMatchInfos, user.getBody());
		return unapliedMatchInfos != null ? ResponseEntity.ok(unapliedMatchInfos) : ResponseEntity.noContent().build();

	}

}
