package com.techchallenger.oficina360;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserRepository {

	private final String url;
	private final String username;
	private final String password;

	// Conexão mantida em nível de instância para reaproveitamento em Warm Starts
	private Connection connection;

	public UserRepository(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}

	// Método auxiliar para garantir que a conexão está ativa antes da query
	private Connection getConnection() throws SQLException {
		if (this.connection == null || this.connection.isClosed()) {
			this.connection = DriverManager.getConnection(url, username, password);
		}
		return this.connection;
	}

	public Optional<User> findByDocument(String document) {
		if (document == null || document.isBlank()) {
			return Optional.empty();
		}

		String sql = """
                SELECT documento, is_active, role
                FROM usuario
                WHERE documento = ?
                """;

		// Removemos a Connection do try-with-resources para NÃO fechá-la a cada chamada
		try (PreparedStatement statement = getConnection().prepareStatement(sql)) {

			statement.setString(1, document);

			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					return Optional.empty();
				}

				return Optional.of(new User(
						resultSet.getString("documento"),
						resultSet.getBoolean("is_active"),
						resultSet.getString("role")
				));
			}
		} catch (SQLException e) {
			// Corrigido de "client" para "user"
			throw new RuntimeException("Error while querying user by document", e);
		}
	}
}
