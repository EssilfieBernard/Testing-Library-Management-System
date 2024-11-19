package com.project.libraryfx.controller;

public class SessionManager {
    private static SessionManager instance;
    private String loggedInUsername;
    private String patronId;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setLoggedInUser(String username, String patronId) {
        this.loggedInUsername = username;
        this.patronId = patronId;
    }

    public String getLoggedInUsername() {
        return loggedInUsername;
    }

    public String getPatronId() {
        return patronId;
    }

    public void clearSession() {
        loggedInUsername = null;
        patronId = null;
    }
}

