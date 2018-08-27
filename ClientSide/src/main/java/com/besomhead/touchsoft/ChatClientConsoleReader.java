package com.besomhead.touchsoft;

import com.besomhead.touchsoft.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.besomhead.touchsoft.ConsoleChatServer.*;

public class ChatClientConsoleReader implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatClientConsoleReader.class);
    private BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
    private User user;

    ChatClientConsoleReader(User user) {
        this.user = user;
    }

    @Override
    public void run() {
        try {
            while (user.isActive()) {
                String userMessage = consoleInput.readLine();
                if (userMessage.startsWith(REGISTER_KEY)) {
                    System.out.println("You may use " + REGISTER_KEY + " only once");
                } else if (userMessage.startsWith(EXIT_KEY)) {
                    user.sendMessageToUser(userMessage);
                    break;
                } else if (userMessage.startsWith(LEAVE_KEY)){
                    continue;
                }
                user.sendMessageToUser(userMessage);
            }
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        } finally {
            user.closeConnection();
        }

        LOGGER.info("User console reader closed");
    }
}
