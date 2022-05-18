package com.mpi.tools.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties
public class UserData {

	@Value("${mpi.user}")
	private String mpiUser;

	@Value("${mpi.password}")
	private String password;

	public UserData() {

	}

	public String getMpiUser() {
		return mpiUser;
	}

	public void setMpiUser(String mpiUser) {
		this.mpiUser = mpiUser;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
