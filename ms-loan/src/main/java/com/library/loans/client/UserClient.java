package com.library.loans.client;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.library.loans.client.dto.UserClientResponse;
import com.library.loans.dto.ApiResponse;
import com.library.loans.exception.ExternalServiceException;
import com.library.loans.exception.ResourceNotFoundException;

@Component
public class UserClient {

    private final RestClient restClient;

    public UserClient(
            RestClient.Builder restClientBuilder,
            @Value("${services.user.base-url:http://localhost:5002}")
            String baseUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Consulta un usuario en el microservicio ms-user.
     *
     * @param userId identificador del usuario
     * @return usuario encontrado
     */
    public UserClientResponse getUserById(UUID userId) {
        try {
            ApiResponse<UserClientResponse> response =
                    restClient.get()
                            .uri("/users/{id}", userId)
                            .retrieve()
                            .body(
                                    new ParameterizedTypeReference<
                                            ApiResponse<UserClientResponse>
                                    >() {
                                    }
                            );

            if (response == null || response.data() == null) {
                throw new ExternalServiceException(
                        "ms-user devolvió una respuesta vacía"
                );
            }

            return response.data();

        } catch (HttpClientErrorException.NotFound exception) {
            throw new ResourceNotFoundException(
                    "Usuario no encontrado con id: " + userId
            );

        } catch (RestClientException exception) {
            throw new ExternalServiceException(
                    "No fue posible comunicarse con ms-user",
                    exception
            );
        }
    }
}
