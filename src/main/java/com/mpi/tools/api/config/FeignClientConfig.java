package com.mpi.tools.api.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;

import com.mpi.tools.MpiToolsApiApplication;
import com.mpi.tools.api.dto.matched.matched.issue.UserDTO;

import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;

public class FeignClientConfig {
	
	private static final String AUTHORIZATION_HEADER = "Authorization";
	
	private static final String TOKEN_TYPE = "Bearer";
	
	private static final String TOKEN = "TOKEN";
	
	/**
	 * Enable this bean if you want to add headers in HTTP request
	 */
	@Bean
	public RequestInterceptor requestInterceptor()
	
	{
		return requestTemplate -> {
			this.feignLoggerLevel();
			requestTemplate.header("Content-Type", "application/fhir+json");
			
			if (requestTemplate.path().contains("mdm-candidate")) {
				requestTemplate.header("Accept", "application/json");
			}
			else {
				requestTemplate.header("Accept", "application/fhir+json");
			}
			
			UserDTO userDto = MpiToolsApiApplication.mainResource.authenticate() ;
			
			requestTemplate.header(AUTHORIZATION_HEADER, TOKEN_TYPE + " " + userDto.getToken());
		};
	}
	
	@Bean
	Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}
	
	@Bean
	@ConditionalOnMissingBean(value = ErrorDecoder.class)
	public FeignClientExceptionErrorDecoder commonFeignErrorDecoder() {
		return new FeignClientExceptionErrorDecoder();
	}
	
	/**
	 * Method to create a bean to increase the timeout value, It is used to overcome the Retryable
	 * exception while invoking the feign client.
	 * 
	 * @param env, An {@link ConfigurableEnvironment}
	 * @return A {@link Request}
	 */
	@Bean
	public static Request.Options requestOptions(ConfigurableEnvironment env) {
		int ribbonReadTimeout = 7000;
		int ribbonConnectionTimeout = 7000;
		
		return new Request.Options(ribbonConnectionTimeout, ribbonReadTimeout);
	}
}
