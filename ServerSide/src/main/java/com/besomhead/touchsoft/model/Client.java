package com.besomhead.touchsoft.model;

public class Client extends User {

    public Client(String name, User user) {
        this.name = name;
        super.setUserSocket(user.getUserSocket());
    }
}
