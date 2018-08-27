package com.besomhead.touchsoft;

import com.besomhead.touchsoft.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.besomhead.touchsoft.ConsoleChatServer.EXIT_KEY;

public class ChatClientConsoleWriter implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatClientConsoleWriter.class);
    private User user;

    ChatClientConsoleWriter(User user) {
        this.user = user;
    }

    @Override
    public void run() {
        while (user.isActive()) {
            String answer = user.getUserMessage();
            if(answer.startsWith(EXIT_KEY)){
                break;
            }
            if (!answer.isEmpty()) {
                System.out.println("-> " + answer);
            }
        }

        LOGGER.info("User console writer closed");
    }
}
