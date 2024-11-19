package com.project.libraryfx.entities;


public class Patron {
    private String username;
    private String memberId;



    public Patron() {};


    public Patron(String memberId, String username) {
        this.memberId = memberId;
        this.username = username;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

}
