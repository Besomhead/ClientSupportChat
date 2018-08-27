package com.besomhead.touchsoft.model;

public class Agent extends User {
    public Agent(String name, User user) {
        this.name = name;
        super.setUserSocket(user.getUserSocket());
    }
}
