package com.mmondino.microbenchmarking.serdes.json.model;

public class User {

    private String givenName;
    private String familyName;

    public User(String givenName, String familyName) {
        this.givenName = givenName;
        this.familyName = familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getFamilyName() {
        return familyName;
    }
}
