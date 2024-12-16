package com.project.libraryfx;

import com.project.libraryfx.controller.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SessionManagerTest {

    private SessionManager sessionManager;

    @BeforeEach
    public void setUp() {
        // Ensure the singleton instance is cleared before each test
        sessionManager = SessionManager.getInstance();
        sessionManager.clearSession();
    }

    @Test
    public void testSingletonInstance() {
        SessionManager instance1 = SessionManager.getInstance();
        SessionManager instance2 = SessionManager.getInstance();

        // Verify that both instances are the same
        assertSame(instance1, instance2);
    }

    @Test
    public void testSetLoggedInUser() {
        sessionManager.setLoggedInUser("testUser", "12345");

        assertEquals("testUser", sessionManager.getLoggedInUsername());
        assertEquals("12345", sessionManager.getPatronId());
    }

    @Test
    public void testClearSession() {
        sessionManager.setLoggedInUser("testUser", "12345");
        sessionManager.clearSession();

        assertNull(sessionManager.getLoggedInUsername());
        assertNull(sessionManager.getPatronId());
    }

    @Test
    public void testGetLoggedInUsernameAndPatronIdInitiallyNull() {
        assertNull(sessionManager.getLoggedInUsername());
        assertNull(sessionManager.getPatronId());
    }
}
