package com.techchallenger.oficina360;

import java.sql.*;
import java.util.Optional;

public class ClientRepository {

	private final String url;
	private final String username;
	private final String password;

	public ClientRepository(
			String url,
			String username,
			String password) {

		this.url = url;
		this.username = username;
		this.password = password;
	}

	public Optional<Client> findByDocument(String document) {

		String sql = """
                SELECT documento, is_active
                FROM USUARIO
                WHERE documento = ?
                """;

		try (
				Connection connection = DriverManager.getConnection(
						url,
						username,
						password
				);

				PreparedStatement statement =
						connection.prepareStatement(sql)
		) {

			statement.setString(1, document);

			try (ResultSet resultSet = statement.executeQuery()) {

				if (!resultSet.next()) {
					return Optional.empty();
				}

				Client client = new Client(
						resultSet.getString("document"),
						resultSet.getBoolean("is_active")
				);

				return Optional.of(client);
			}

		} catch (SQLException e) {
			throw new RuntimeException(
					"Error while querying client",
					e
			);
		}
	}
}