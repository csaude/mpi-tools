package com.mpi.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.mpi.tools.api.resource.MatchedResource;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class MpiToolsApiApplication {

	@Autowired
	private MatchedResource matchedResource;

	public static void main(String[] args) {
		SpringApplication.run(MpiToolsApiApplication.class, args);
	}

	@Bean
	public CommandLineRunner CommandLineRunnerBean() {
		return (args) -> {
			System.out.println("Finding Match Patient");

			// First Login
			// @SuppressWarnings("unchecked")
			// ResponseEntity<UserDTO> user = (ResponseEntity<UserDTO>)
			// matchedResource.authenticateUser();
			matchedResource.findAllMatched(null);

			// Resolver inconsistencias
			matchedResource.resolveUnapliedMatch();
			System.out.println("Done Finding Match Patient");

		};
	}

}
