package com.besomhead.touchsoft;

import com.besomhead.touchsoft.model.Agent;
import com.besomhead.touchsoft.model.Client;
import com.besomhead.touchsoft.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

import static com.besomhead.touchsoft.ConsoleChatServer.*;

public class ChatServerThread implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatServerThread.class);
    private static final int KEY_INDEX = 0;
    private static final int USER_TYPE_INDEX = 1;
    private static final int USER_NAME_INDEX = 2;

    private User user;

    ChatServerThread(Socket userSocket) {
        user = new User(userSocket);
    }

    private void registerClient(String name) {
        user = new Client(name, user);
        LOGGER.info("Client " + name + " registered");
    }

    private void registerAgent(String name) {
        user = new Agent(name, user);
        saveAgent((Agent) user);
        LOGGER.info("Agent " + name + " registered");
    }

    private void manageConversation() {
        Agent agent = getVacantAgent();
        boolean saveSession = false;
        LOGGER.info("Start conversation between agent " + agent.getName() + " and client " + user.getName());
        while (user.isActive()) {
            String userMessage = user.getUserMessage();
            String agentMessage = agent.getUserMessage();
            if(userMessage == null || agentMessage == null){
                return;
            }
            if (userMessage.startsWith(EXIT_KEY)) {
                LOGGER.info("Client " + user.getName() + " disconnected");
                user.sendMessageToUser(userMessage);
                agent.sendMessageToUser("Client disconnected");
                saveAgent(agent);
                saveSession = false;
            }
            if (agentMessage.startsWith(EXIT_KEY)) {
                LOGGER.info("Agent " + agent.getName() + " disconnected");
                agent.sendMessageToUser(agentMessage);
                removeAgent(agent);
                user.sendMessageToUser("Agent disconnected");
                saveSession = true;
            }
            if (userMessage.startsWith(LEAVE_KEY)) {
                agent.sendMessageToUser("Client ended conversation");
                saveSession = true;
                saveAgent(agent);
            }

            agent.sendMessageToUser(user.getUserMessage());
            user.sendMessageToUser(agent.getUserMessage());
        }

        if (saveSession) {
            user.getUserMessage();
            manageConversation();
        }
        LOGGER.info("Finish conversation between agent " + agent.getName() + " and client " + user.getName());
    }

    @Override
    public void run() {
        String[] userMessageParts = user.getUserMessage().split(" ");

        if (!userMessageParts[KEY_INDEX].equalsIgnoreCase(REGISTER_KEY)) {
            LOGGER.warn("First command should be " + REGISTER_KEY);
            return;
        }

        switch (userMessageParts[USER_TYPE_INDEX]) {
            case USER_TYPE_AGENT:
                registerAgent(userMessageParts[USER_NAME_INDEX]);
                return;
            case USER_TYPE_CLIENT:
                registerClient(userMessageParts[USER_NAME_INDEX]);
                break;
            default:
                LOGGER.warn("Unsupported user type");
                user.sendMessageToUser("Please choose " + USER_TYPE_CLIENT + " or " + USER_TYPE_AGENT);
                return;
        }
        manageConversation();
    }
}
