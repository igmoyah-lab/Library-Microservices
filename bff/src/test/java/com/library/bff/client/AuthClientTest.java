package com.library.bff.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.bff.dto.AuthResponse;
import com.library.bff.dto.LoginRequest;
import com.library.bff.dto.RegisterRequest;

class AuthClientTest {

    private static final String AUTH_BASE_URL = "http://localhost:5001/api/auth";

    private MockRestServiceServer mockServer;
    private AuthClient authClient;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        RestClient.Builder restClientBuilder = RestClient.builder();

        mockServer = MockRestServiceServer
                .bindTo(restClientBuilder)
                .build();

        RestClient restClient = restClientBuilder.build();

        authClient = new AuthClient(
                restClient,
                AUTH_BASE_URL,
                objectMapper
        );
    }

    @Test
    void login_deberiaRetornarAuthResponseCuandoLasCredencialesSonCorrectas() {
        // Arrange
        LoginRequest request = new LoginRequest(
                "user1@mail.cl",
                "abcd.1234"
        );

        String responseJson = """
                {
                    "token": "token-jwt-login"
                }
                """;

        mockServer.expect(requestTo(AUTH_BASE_URL + "/login"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                            "email": "user1@mail.cl",
                            "password": "abcd.1234"
                        }
                        """))
                .andRespond(withSuccess(
                        responseJson,
                        MediaType.APPLICATION_JSON
                ));

        // Act
        AuthResponse response = authClient.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("token-jwt-login", response.token());

        mockServer.verify();
    }

    @Test
    void login_deberiaLanzarResponseStatusExceptionCuandoLasCredencialesSonInvalidas() {
        // Arrange
        LoginRequest request = new LoginRequest(
                "user1@mail.cl",
                "password-incorrecta"
        );

        String errorJson = """
                {
                    "success": false,
                    "message": "Credenciales inválidas"
                }
                """;

        mockServer.expect(requestTo(AUTH_BASE_URL + "/login"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withUnauthorizedRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorJson));

        // Act
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authClient.login(request)
        );

        // Assert
        assertEquals(401, exception.getStatusCode().value());
        assertEquals("Credenciales inválidas", exception.getReason());

        mockServer.verify();
    }

    @Test
    void register_deberiaRetornarAuthResponseCuandoElUsuarioEsRegistrado() {
        // Arrange
        RegisterRequest request = new RegisterRequest(
                "nuevo@mail.cl",
                "abcd.1234"
        );

        String responseJson = """
                {
                    "token": "token-jwt-register"
                }
                """;

        mockServer.expect(requestTo(AUTH_BASE_URL + "/register"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                            "email": "nuevo@mail.cl",
                            "password": "abcd.1234"
                        }
                        """))
                .andRespond(withSuccess(
                        responseJson,
                        MediaType.APPLICATION_JSON
                ));

        // Act
        AuthResponse response = authClient.register(request);

        // Assert
        assertNotNull(response);
        assertEquals("token-jwt-register", response.token());

        mockServer.verify();
    }

    @Test
    void register_deberiaObtenerMensajeDesdeLaPropiedadMessage() {
        // Arrange
        RegisterRequest request = new RegisterRequest(
                "existente@mail.cl",
                "abcd.1234"
        );

        String errorJson = """
                {
                    "success": false,
                    "message": "El correo ya se encuentra registrado"
                }
                """;

        mockServer.expect(requestTo(AUTH_BASE_URL + "/register"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorJson));

        // Act
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authClient.register(request)
        );

        // Assert
        assertEquals(400, exception.getStatusCode().value());
        assertEquals(
                "El correo ya se encuentra registrado",
                exception.getReason()
        );

        mockServer.verify();
    }

    @Test
    void register_deberiaObtenerMensajeDesdeLaPropiedadError() {
        // Arrange
        RegisterRequest request = new RegisterRequest(
                "correo-invalido",
                "abcd.1234"
        );

        String errorJson = """
                {
                    "error": "Formato de correo inválido"
                }
                """;

        mockServer.expect(requestTo(AUTH_BASE_URL + "/register"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorJson));

        // Act
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authClient.register(request)
        );

        // Assert
        assertEquals(400, exception.getStatusCode().value());
        assertEquals(
                "Formato de correo inválido",
                exception.getReason()
        );

        mockServer.verify();
    }

    @Test
    void login_deberiaRetornarElCuerpoCompletoCuandoNoExisteMessageNiError() {
        // Arrange
        LoginRequest request = new LoginRequest(
                "user1@mail.cl",
                "password-incorrecta"
        );

        String errorJson = """
                {
                    "detalle": "Usuario bloqueado"
                }
                """;

        mockServer.expect(requestTo(AUTH_BASE_URL + "/login"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorJson));

        // Act
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authClient.login(request)
        );

        // Assert
        assertEquals(400, exception.getStatusCode().value());
        assertEquals(errorJson, exception.getReason());

        mockServer.verify();
    }

    @Test
    void login_deberiaRetornarMensajeGenericoCuandoElJsonEsInvalido() {
        // Arrange
        LoginRequest request = new LoginRequest(
                "user1@mail.cl",
                "password-incorrecta"
        );

        mockServer.expect(requestTo(AUTH_BASE_URL + "/login"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{json-invalido"));

        // Act
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authClient.login(request)
        );

        // Assert
        assertEquals(500, exception.getStatusCode().value());
        assertEquals(
                "Error al comunicarse con ms-auth",
                exception.getReason()
        );

        mockServer.verify();
    }
}