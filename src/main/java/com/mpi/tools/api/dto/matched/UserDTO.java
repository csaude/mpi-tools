package com.mpi.tools.api.dto.matched;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDTO {

	private String token;

	private String role;

	private int userID;

	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@JsonProperty("token")
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
