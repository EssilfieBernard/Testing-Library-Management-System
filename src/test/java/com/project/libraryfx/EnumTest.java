package com.project.libraryfx;

import com.project.libraryfx.entities.BookStatus;
import com.project.libraryfx.entities.TransactionStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EnumTest {

    @Test
    public void testBookStatusToString() {
        assertEquals("AVAILABLE", BookStatus.AVAILABLE.toString());
        assertEquals("CHECKED_OUT", BookStatus.CHECKED_OUT.toString());
        assertEquals("RESERVED", BookStatus.RESERVED.toString());
        assertEquals("LOST", BookStatus.LOST.toString());
        assertEquals("DAMAGED", BookStatus.DAMAGED.toString());
    }

    @Test
    public void testTransactionStatusValues() {
        assertEquals("ACTIVE", TransactionStatus.ACTIVE.name());
        assertEquals("COMPLETED", TransactionStatus.COMPLETED.name());
        assertEquals("OVERDUE", TransactionStatus.OVERDUE.name());
        assertEquals("LOST", TransactionStatus.LOST.name());
    }
}
