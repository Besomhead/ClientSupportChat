package com.besomhead.touchsoft;


import com.besomhead.touchsoft.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import static com.besomhead.touchsoft.ConsoleChatServer.*;

public class ConsoleChatClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleChatClient.class);

    private boolean isRegistered = false;

    private void initConversation() {
        User user = new User();
        try {
            user = new User(new Socket("localhost", 9876));

            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

            while (!isRegistered) {
                String userMessage = consoleInput.readLine();

                if (!userMessage.startsWith(REGISTER_KEY)) {
                    System.out.println("The first command should be " + REGISTER_KEY);
                } else {
                    if (!userMessage.contains(USER_TYPE_AGENT) && !userMessage.contains(USER_TYPE_CLIENT)) {
                        System.out.println("Please choose " + USER_TYPE_AGENT + " or " + USER_TYPE_CLIENT);
                    } else {
                        isRegistered = true;
                        user.sendMessageToUser(userMessage);
                    }
                }
            }
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        }

        Thread readerThread = new Thread(new ChatClientConsoleReader(user), "Client console reader");
        Thread writerThread = new Thread(new ChatClientConsoleWriter(user), "Client console writer");
        readerThread.start();
        writerThread.start();
    }

    public static void main(String[] args) {
        new ConsoleChatClient().initConversation();
    }
}
