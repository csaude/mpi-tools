package com.mpi.tools.api.resource.interfaces;

import com.mpi.tools.api.config.UserData;
import com.mpi.tools.api.dto.matched.matched.issue.UserDTO;

public interface Authenticator {
	public UserDTO autenticate(UserData userData);
}
