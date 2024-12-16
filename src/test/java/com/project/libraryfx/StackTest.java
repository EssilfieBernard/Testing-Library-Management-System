package com.project.libraryfx;

import com.project.libraryfx.data_structures.Stack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StackTest {

    private Stack<Integer> stack;

    @BeforeEach
    public void setUp() {
        stack = new Stack<>();
    }

    @Test
    public void testPush() {
        stack.push(1);
        stack.push(2);

        assertEquals(2, stack.size());
        assertEquals(2, stack.peek()); // The last pushed element should be on top
    }

    @Test
    public void testPop() {
        stack.push(1);
        stack.push(2);

        int poppedElement = stack.pop();

        assertEquals(2, poppedElement); // The last pushed element should be removed first
        assertEquals(1, stack.size());
        assertEquals(1, stack.peek()); // The next element should now be on top
    }

    @Test
    public void testPeek() {
        stack.push(1);
        stack.push(2);

        assertEquals(2, stack.peek()); // The last pushed element should be on top

        stack.pop(); // Remove the top element

        assertEquals(1, stack.peek()); // The next element should now be on top
    }

    @Test
    public void testIsEmpty() {
        assertTrue(stack.isEmpty()); // Stack should be empty initially

        stack.push(1);

        assertFalse(stack.isEmpty()); // Stack should not be empty after pushing an element

        stack.pop();

        assertTrue(stack.isEmpty()); // Stack should be empty after popping the only element
    }

    @Test
    public void testSize() {
        assertEquals(0, stack.size()); // Initial size should be 0

        stack.push(1);
        stack.push(2);

        assertEquals(2, stack.size()); // Size should reflect the number of elements pushed

        stack.pop();

        assertEquals(1, stack.size()); // Size should decrease after a pop operation
    }
}

