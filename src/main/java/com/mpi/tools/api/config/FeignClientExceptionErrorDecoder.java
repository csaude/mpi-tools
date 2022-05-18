package com.mpi.tools.api.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feign.FeignException.FeignClientException;
import feign.Response;
import feign.codec.ErrorDecoder;
import feign.codec.StringDecoder;

/**
 * Decodes a failed request response building a FeignClientException Any error
 * decoding the response body will be thrown as a
 * FeignClientErrorDecodingException
 * 
 * @author patrick.way
 */
public class FeignClientExceptionErrorDecoder implements ErrorDecoder {
	private static final Logger LOGGER = LoggerFactory.getLogger(FeignClientExceptionErrorDecoder.class);
	private StringDecoder stringDecoder = new StringDecoder();

	@Override
	public FeignClientException decode(final String methodKey, Response response) {
		String message = "Null Response Body.";
		if (response.body() != null) {
			try {
				message = stringDecoder.decode(response, String.class).toString();
			} catch (IOException e) {
				LOGGER.error(methodKey + "Error Deserializing response body from failed feign request response.", e);
			}
		}

		LOGGER.info("REQUEST FROM CLIENTT " + response.request());
		return new FeignClientException(response.status(), message, response.request(), response.reason().getBytes());
	}
}
