package com.techchallenger.oficina360;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

import static java.lang.String.format;

public class Handler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private final JwtService jwtService;
	private final DocumentValidator documentValidator;
	private final ClientRepository clientRepository;
	private final ObjectMapper objectMapper;

	public Handler() {

		this.jwtService = new JwtService(System.getenv("secret"), Long.parseLong(System.getenv("expiration")));

		this.documentValidator = new DocumentValidator();

		this.clientRepository = new ClientRepository(System.getenv("DB_URL"), System.getenv("DB_USERNAME"),
				System.getenv("DB_PASSWORD"));

		this.objectMapper = new ObjectMapper();
	}

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {

		AuthRequest authRequest;
		context.getLogger().log(
				"BODY RECEIVED: " + input.getBody()
		);
		try {
			authRequest = objectMapper.readValue(input.getBody(), AuthRequest.class);
			context.getLogger().log(
					"CPF RECEIVED: " + authRequest.normalizedDocument()
			);
		} catch (JsonProcessingException e) {
			context.getLogger().log(
					"JSON ERROR: " + e.getMessage()
			);
			return badRequest();
		}

		String document = authRequest.normalizedDocument();

		if (!documentValidator.execute(document)) {
			return unauthorized("Documento invalido");
		}

		Client client = clientRepository.findByDocument(document).orElse(null);

		if (client == null || !client.isActive()) {
			return unauthorized("Cliente esta inativo");
		}

		AuthResponse authResponse = jwtService.generateToken(client.document());

		return ok(authResponse);
	}

	private APIGatewayProxyResponseEvent ok(AuthResponse response) {

		try {

			String body = objectMapper.writeValueAsString(response);

			return new APIGatewayProxyResponseEvent().withStatusCode(200)
					.withHeaders(Map.of("Content-Type", "application/json")).withBody(body);

		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error serializing authentication response", e);
		}
	}

	private APIGatewayProxyResponseEvent badRequest() {

		return new APIGatewayProxyResponseEvent().withStatusCode(400)
				.withHeaders(Map.of("Content-Type", "application/json")).withBody("""
						{"message":"Invalid request"}
						""");
	}

	private APIGatewayProxyResponseEvent unauthorized(String msg) {

		return new APIGatewayProxyResponseEvent().withStatusCode(401)
				.withHeaders(Map.of("Content-Type", "application/json")).withBody(format("""
						{"message":"Unauthorized: %s"}
						""",msg));
	}
}