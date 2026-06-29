package com.library.bff.client;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.bff.dto.AuthResponse;
import com.library.bff.dto.LoginRequest;
import com.library.bff.dto.RegisterRequest;

@Component
public class AuthClient {

    private final RestClient restClient;
    private final String authBaseUrl;
    private final ObjectMapper objectMapper;

    public AuthClient(
            RestClient restClient,
            @Value("${auth.service.base-url}") String authBaseUrl,
            ObjectMapper objectMapper
    ) {
        this.restClient = restClient;
        this.authBaseUrl = authBaseUrl;
        this.objectMapper = objectMapper;
    }

    public AuthResponse login(LoginRequest request) {
        return restClient.post()
                .uri(authBaseUrl + "/login")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (httpRequest, response) -> {
                    throw new ResponseStatusException(
                            response.getStatusCode(),
                            extractMessage(response)
                    );
                })
                .body(AuthResponse.class);
    }

    public AuthResponse register(RegisterRequest request) {
        return restClient.post()
                .uri(authBaseUrl + "/register")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (httpRequest, response) -> {
                    throw new ResponseStatusException(
                            response.getStatusCode(),
                            extractMessage(response)
                    );
                })
                .body(AuthResponse.class);
    }

    private String extractMessage(org.springframework.http.client.ClientHttpResponse response) {
        try {
            String body = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);

            if (body == null || body.isBlank()) {
                return response.getStatusText();
            }

            JsonNode json = objectMapper.readTree(body);

            if (json.has("message")) {
                return json.get("message").asText();
            }

            if (json.has("error")) {
                return json.get("error").asText();
            }

            return body;

        } catch (Exception e) {
            return "Error al comunicarse con ms-auth";
        }
    }
}