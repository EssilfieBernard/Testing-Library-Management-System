package com.project.libraryfx;

import com.project.libraryfx.entities.Patron;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PatronTest {

    @Test
    public void testDefaultConstructor() {
        Patron patron = new Patron();

        assertNull(patron.getUsername());
        assertNull(patron.getMemberId());
    }

    @Test
    public void testParameterizedConstructor() {
        Patron patron = new Patron("12345", "JohnDoe");

        assertEquals("JohnDoe", patron.getUsername());
        assertEquals("12345", patron.getMemberId());
    }

    @Test
    public void testSettersAndGetters() {
        Patron patron = new Patron();

        patron.setUsername("JaneDoe");
        patron.setMemberId("67890");

        assertEquals("JaneDoe", patron.getUsername());
        assertEquals("67890", patron.getMemberId());
    }
}
