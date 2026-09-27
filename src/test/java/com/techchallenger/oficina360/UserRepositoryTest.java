package com.techchallenger.oficina360;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserRepositoryTest {

	@Test
	void shouldReturnEmptyWhenDocumentIsNull() {

		UserRepository repository = new UserRepository("jdbc:postgresql://localhost/test", "user", "password");

		Optional<User> result = repository.findByDocument(null);

		assertTrue(result.isEmpty());
	}

	@Test
	void shouldReturnEmptyWhenDocumentIsBlank() {

		UserRepository repository = new UserRepository("jdbc:postgresql://localhost/test", "user", "password");

		Optional<User> result = repository.findByDocument("   ");

		assertTrue(result.isEmpty());
	}

	@Test
	void shouldReturnUserWhenDocumentExists() throws Exception {

		Connection connection = mock(Connection.class);
		PreparedStatement statement = mock(PreparedStatement.class);
		ResultSet resultSet = mock(ResultSet.class);

		try (MockedStatic<DriverManager> mockedDriverManager = Mockito.mockStatic(DriverManager.class)) {

			mockedDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
					.thenReturn(connection);

			when(connection.isClosed()).thenReturn(false);

			when(connection.prepareStatement(anyString())).thenReturn(statement);

			when(statement.executeQuery()).thenReturn(resultSet);

			when(resultSet.next()).thenReturn(true);

			when(resultSet.getString("documento")).thenReturn("12345678901");

			when(resultSet.getBoolean("is_active")).thenReturn(true);

			when(resultSet.getString("role")).thenReturn("CLIENT");

			UserRepository repository = new UserRepository("jdbc:postgresql://localhost/test", "user", "password");

			Optional<User> result = repository.findByDocument("12345678901");

			assertTrue(result.isPresent());

			User user = result.get();

			assertEquals("12345678901", user.document());

			assertTrue(user.isActive());

			assertEquals("CLIENT", user.role());

			verify(statement).setString(1, "12345678901");
		}
	}

	@Test
	void shouldReturnEmptyWhenUserDoesNotExist() throws Exception {

		Connection connection = mock(Connection.class);
		PreparedStatement statement = mock(PreparedStatement.class);
		ResultSet resultSet = mock(ResultSet.class);

		try (MockedStatic<DriverManager> mockedDriverManager = Mockito.mockStatic(DriverManager.class)) {

			mockedDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
					.thenReturn(connection);

			when(connection.isClosed()).thenReturn(false);

			when(connection.prepareStatement(anyString())).thenReturn(statement);

			when(statement.executeQuery()).thenReturn(resultSet);

			when(resultSet.next()).thenReturn(false);

			UserRepository repository = new UserRepository("jdbc:postgresql://localhost/test", "user", "password");

			Optional<User> result = repository.findByDocument("12345678901");

			assertTrue(result.isEmpty());
		}
	}

	@Test
	void shouldReuseExistingConnection() throws Exception {

		Connection connection = mock(Connection.class);
		PreparedStatement statement = mock(PreparedStatement.class);
		ResultSet resultSet = mock(ResultSet.class);

		try (MockedStatic<DriverManager> mockedDriverManager = Mockito.mockStatic(DriverManager.class)) {

			mockedDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
					.thenReturn(connection);

			when(connection.isClosed()).thenReturn(false);

			when(connection.prepareStatement(anyString())).thenReturn(statement);

			when(statement.executeQuery()).thenReturn(resultSet);

			when(resultSet.next()).thenReturn(false);

			UserRepository repository = new UserRepository("jdbc:postgresql://localhost/test", "user", "password");

			repository.findByDocument("12345678901");
			repository.findByDocument("98765432100");

			mockedDriverManager.verify(() -> DriverManager.getConnection(anyString(), anyString(), anyString()),
					times(1));
		}
	}
}