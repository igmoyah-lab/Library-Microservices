package com.library.bff.services;

import org.springframework.stereotype.Service;

import com.library.bff.client.AuthClient;
import com.library.bff.dto.AuthResponse;
import com.library.bff.dto.LoginRequest;
import com.library.bff.dto.RegisterRequest;

@Service
public class AuthService {

	private final AuthClient authClient;

	public AuthService(AuthClient authClient) {
		this.authClient = authClient;
	}

	public AuthResponse login(LoginRequest request) {
		return authClient.login(request);
	}

	public AuthResponse register(RegisterRequest request) {
		return authClient.register(request);
	}
}
