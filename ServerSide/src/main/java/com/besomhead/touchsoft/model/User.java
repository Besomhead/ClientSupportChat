package com.besomhead.touchsoft.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class User {
    private static final Logger LOGGER = LoggerFactory.getLogger(User.class);

    private Socket userSocket;
    private BufferedWriter socketOutput;
    private BufferedReader socketInput;
    String name;

    public User() {

    }

    public User(Socket socket) {
        userSocket = socket;
        getSocketOutput();
        getSocketInput();
    }

    public String getName() {
        return name;
    }

    protected Socket getUserSocket() {
        return userSocket;
    }

    protected void setUserSocket(Socket socket) {
        userSocket = socket;
    }

    private BufferedReader getSocketInput() {
        if (socketInput != null) {
            return socketInput;
        }

        try {
            socketInput = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        }

        return socketInput;
    }

    private BufferedWriter getSocketOutput() {
        if (socketOutput != null) {
            return socketOutput;
        }

        try {
            socketOutput = new BufferedWriter(new OutputStreamWriter(userSocket.getOutputStream()));
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        }

        return socketOutput;
    }

    public boolean isActive() {
        return !userSocket.isClosed() && !userSocket.isOutputShutdown();
    }

    public String getUserMessage() {
        String userMessage = "";

        try {
            if (getSocketInput() == null) {
                LOGGER.warn("Can't get user socket input");
                return userMessage;
            }

            userMessage = getSocketInput().readLine();
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        }

        return userMessage;
    }

    public void sendMessageToUser(String agentMessage) {
        BufferedWriter out = getSocketOutput();
        if (out == null) {
            LOGGER.warn("Can't get user socket output");
            return;
        }

        try {
            out.write(agentMessage + System.lineSeparator());
            out.flush();
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    private void closeResource(Closeable resource) {
        try {
            resource.close();
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        }

    }

    public void closeConnection() {
        if (userSocket.isClosed()) {
            return;
        }

        closeResource(getSocketInput());
        closeResource(getSocketOutput());
        closeResource(userSocket);
    }

    public String toString() {
        return this instanceof Agent ? "Agent: " : "Client: " + getName();
    }
}
