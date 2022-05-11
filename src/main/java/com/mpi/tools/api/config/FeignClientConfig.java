package com.mpi.tools.api.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;

public class FeignClientConfig {

	/**
	 * Enable this bean if you want to add headers in HTTP request
	 */
	@Bean
	public RequestInterceptor requestInterceptor() {
		return requestTemplate -> {
			requestTemplate.header("Content-Type", "[application/fhir+json;charset=utf-8]");
			requestTemplate.header("Accept", "application/json");
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
}
