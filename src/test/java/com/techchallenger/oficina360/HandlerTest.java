package com.techchallenger.oficina360;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	@Mock
	private JwtService jwtService;
	@Mock
	private DocumentValidator documentValidator;
	@Mock
	private UserRepository userRepository;
	@Mock
	private Context context;
	@Mock
	private Context logger;
	private Handler handler;

	@BeforeEach
	void setup() {

		handler = new Handler(jwtService, documentValidator, userRepository, objectMapper);
	}


	@Test
	void shouldReturn401WhenDocumentIsInvalid() {

		String body = """
				{
				   "document":"12345678900"
				}
				""";

		APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent().withBody(body);

		when(documentValidator.execute(anyString())).thenReturn(false);

		APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

		assertEquals(401, response.getStatusCode());

		assertTrue(response.getBody().contains("Documento invalido"));
	}

	@Test
	void shouldReturn401WhenUserNotFound() {

		String body = """
				{
				   "document":"12345678901"
				}
				""";

		APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent().withBody(body);

		when(documentValidator.execute(anyString())).thenReturn(true);

		when(userRepository.findByDocument(anyString())).thenReturn(Optional.empty());

		APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

		assertEquals(401, response.getStatusCode());

		assertTrue(response.getBody().contains("Cliente esta inativo"));
	}

	@Test
	void shouldReturn401WhenUserIsInactive() {

		String body = """
				{
				   "document":"12345678901"
				}
				""";

		User user = mock(User.class);

		when(user.isActive()).thenReturn(false);

		APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent().withBody(body);

		when(documentValidator.execute(anyString())).thenReturn(true);

		when(userRepository.findByDocument(anyString())).thenReturn(Optional.of(user));

		APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

		assertEquals(401, response.getStatusCode());

		assertTrue(response.getBody().contains("Cliente esta inativo"));
	}

	@Test
	void shouldReturn200AndTokenWhenUserIsValid() {

		String body = """
				{
				   "document":"12345678901"
				}
				""";

		User user = mock(User.class);

		when(user.isActive()).thenReturn(true);
		when(user.document()).thenReturn("12345678901");

		AuthResponse authResponse = new AuthResponse("jwt-token","Bearer", 123456L);

		when(documentValidator.execute(anyString())).thenReturn(true);

		when(userRepository.findByDocument(anyString())).thenReturn(Optional.of(user));

		when(jwtService.generateToken("12345678901")).thenReturn(authResponse);

		APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent().withBody(body);

		APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

		assertEquals(200, response.getStatusCode());

		assertTrue(response.getBody().contains("jwt-token"));

		verify(jwtService).generateToken("12345678901");
	}
}