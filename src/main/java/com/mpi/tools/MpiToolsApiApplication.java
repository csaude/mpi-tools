package com.mpi.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.mpi.tools.api.resource.MatchedResource;
import com.mpi.tools.api.resource.interfaces.Authenticator;
import com.mpi.tools.api.utils.OpenCrAuthenticator;
import com.mpi.tools.api.utils.SanteMpiAuthenticator;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class MpiToolsApiApplication {

	@Autowired
	private MatchedResource matchedResource;

	@Autowired
	private OpenCrAuthenticator openCruserAuthenticator;
	
	@Autowired
	private SanteMpiAuthenticator santeMpiAuthenticator;
	
	public static MatchedResource mainResource;
	
	@Value("${mpi.tools.action}")
	private String action;
	
	public static final String ACTION_FIND_MATCH="find_match";
	public static final String ACTION_PROCESS_MATCH="process_match";
	
	
	public static void main(String[] args) {
		SpringApplication.run(MpiToolsApiApplication.class, args);
	}

	
	
	@Bean
	public CommandLineRunner CommandLineRunnerBean() {
		return (args) -> {
			System.out.println("Finding Match Patient");

			mainResource = matchedResource;
			
			if (action.equals(ACTION_FIND_MATCH)) {
				matchedResource.findAllMatched(matchedResource.authenticate());
				System.out.println("Done Finding Match Patient");
			}
			else
			if (action.equals(ACTION_PROCESS_MATCH)) {
				matchedResource.resolveUnapliedMatch();
				System.out.println("Done Processing Match Patient");		
			}
			else throw new RuntimeException("Action not suported");
			
			// Resolver inconsistencias
			
		};
	}

	public Authenticator getAutenticator(String system) {
		if (system.equals("santempi")) {
			return santeMpiAuthenticator;
		}
		else
		if (system.equals("opencr")) {
			return openCruserAuthenticator;
		}
		else throw new RuntimeException("The specified system is unsupported. Should use ['santempi', 'opencr'] ");
	}	
}
