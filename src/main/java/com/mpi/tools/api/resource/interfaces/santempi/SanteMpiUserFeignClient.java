package com.mpi.tools.api.resource.interfaces.santempi;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mpi.tools.api.config.FeignClientConfig;
import com.mpi.tools.api.dto.matched.matched.issue.UserDTO;
import com.mpi.tools.api.resource.interfaces.UserFeignClient;

@FeignClient(name = "santeMpiUserAuthenticationFeignClient", configuration = FeignClientConfig.class, url = "${mpi.ocrux.host}/ocrux")
public interface SanteMpiUserFeignClient extends UserFeignClient{

	@PostMapping("/user/authenticate")
	public UserDTO authenticateUser(@RequestParam("username") String username,
			@RequestParam("password") String password);

}
