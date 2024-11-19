package com.project.libraryfx.entities;

public enum BookStatus {
    AVAILABLE, CHECKED_OUT, RESERVED, LOST, DAMAGED;

    @Override
    public String toString() {
        return name();
    }
}
