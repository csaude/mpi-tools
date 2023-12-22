package com.mpi.tools.api.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpi.tools.api.config.UserData;
import com.mpi.tools.api.dto.matched.matched.issue.UserDTO;
import com.mpi.tools.api.resource.interfaces.Authenticator;

@Component
public class SanteMpiAuthenticator implements Authenticator {	
	@Value("${mpi.sante.db.client.id}")
	private String clientId;
	
	@Value("${mpi.sante.db.client.secret}")
	private String clientSecret;
	
	@Value("${mpi.server.base.url}")
	private String serverBaseUrl;

	private TokenInfo tokenInfo;
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	@Override
	public UserDTO autenticate(UserData userData) {
		try {
			retriveAccessToken();
		
			UserDTO u = new UserDTO();
			
			u.setUsername(clientId);
			u.setToken(this.tokenInfo.getAccessToken());
			
			return u;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		
		
	}
	
	public TokenInfo getTokenInfo() {
		return tokenInfo;
	}
	
	public String getClientSecret() {
		return clientSecret;
	}
	
	public String getClientId() {
		return clientId;
	}
	
	public String getServerBaseUrl() {
		return serverBaseUrl;
	}
	
	private <T> void retriveAccessToken() throws Exception {
		
		String data = "grant_type=client_credentials" + "&" + "scope=*" + "&" + "client_secret="
		        + getClientSecret() + "&" + "client_id=" + getClientId();
		
		// Request a Refresh token in case it expires 
		if (getTokenInfo() != null) {
			
			if (!getTokenInfo().isValid(LocalDateTime.now())) {
				//Implement a refresh token method
				data = "grant_type=refresh_token&refresh_token=" + getTokenInfo().getRefreshToken() + "&"
				        + "client_secret=" + getClientSecret() + "&" + "client_id=" + getClientId();
				this.doAuthentication(data);
			}
		} else {
			// Normal Login 
			this.doAuthentication(data);
		}
	}
	
	protected void doAuthentication(String data) throws Exception {
		String uri = "/auth/oauth2_token";
		String url = getServerBaseUrl() + uri;
		
		HttpURLConnection connection = openConnection(url);
		
		//Range<Integer> successRange = Range.between(200, 299);
		
		try {
			connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length", Integer.toString(data.getBytes().length));
			
			connection.setConnectTimeout(30000);
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			
			connection.connect();
			
			OutputStream out = connection.getOutputStream();
			out.write(data.getBytes());
			out.flush();
			out.close();
			
			if (connection.getResponseCode() != 200) {
				final String error = connection.getResponseCode() + " " + connection.getResponseMessage();
				throw new RuntimeException("Unexpected response " + error + " from MPI");
			}
			
			initToken(MAPPER.readValue(connection.getInputStream(), TokenInfo.class));
		}
		finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
	
	public void initToken(TokenInfo tokenInfo) {
		this.tokenInfo = tokenInfo;
		this.tokenInfo
		        .setTokenExpirationDateTime(LocalDateTime.now().plus(this.tokenInfo.getExpiresIn(), ChronoUnit.MILLIS));
	}
	
	public static HttpURLConnection openConnection(String url) throws IOException {
		return (HttpURLConnection) new URL(url).openConnection();
	}
	
}

