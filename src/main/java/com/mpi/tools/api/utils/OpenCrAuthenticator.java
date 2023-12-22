package com.mpi.tools.api.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mpi.tools.api.config.UserData;
import com.mpi.tools.api.dto.matched.matched.issue.UserDTO;
import com.mpi.tools.api.resource.interfaces.Authenticator;
import com.mpi.tools.api.resource.interfaces.opencr.OpenCrUserFeignClient;


@Component
public class OpenCrAuthenticator implements Authenticator {
	
	@Autowired
	private OpenCrUserFeignClient client;
	
	@Override
	public UserDTO autenticate(UserData userData) {
		UserDTO dto = client.authenticateUser(userData.getMpiUser(), userData.getPassword());
		System.out.println("userdata " + userData.getPassword().toString() + " - " + userData.getMpiUser());

		System.out.print("PRINT USER SESSIONS " + dto.getToken());
		
		return dto;
		//return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.noContent().build();
	}
	
}
