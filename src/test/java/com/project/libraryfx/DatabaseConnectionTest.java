package com.project.libraryfx;


import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class DatabaseConnectionTest {

    @BeforeEach
    public void setUp() {
        // Ensure that Dotenv and DriverManager are mocked before each test
        Dotenv.load();
    }

    @Test
    public void testGetConnection_Success() throws SQLException {
        // Mock environment variables
        try (MockedStatic<Dotenv> dotenvMock = Mockito.mockStatic(Dotenv.class);
             MockedStatic<DriverManager> driverManagerMock = Mockito.mockStatic(DriverManager.class)) {

            Dotenv dotenv = mock(Dotenv.class);
            dotenvMock.when(Dotenv::load).thenReturn(dotenv);
            when(dotenv.get("DB_URL")).thenReturn("jdbc:mysql://localhost/test");
            when(dotenv.get("DB_USER")).thenReturn("user");
            when(dotenv.get("DB_PASSWORD")).thenReturn("password");

            // Mock DriverManager.getConnection to return a mock Connection
            Connection mockConnection = mock(Connection.class);
            driverManagerMock.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);

            // Call the method under test
            Connection connection = DatabaseConnection.getConnection();

            // Verify the connection is returned and not null
            assertNotNull(connection);
            assertSame(mockConnection, connection);

            // Verify that getConnection was called with correct parameters
            driverManagerMock.verify(() -> DriverManager.getConnection(
                    "jdbc:mysql://localhost/test", "user", "password"), times(1));
        }
    }


}

