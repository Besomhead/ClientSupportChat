package com.besomhead.touchsoft;

import com.besomhead.touchsoft.model.Client;

import javax.websocket.Session;

import static com.besomhead.touchsoft.ConsoleChatServer.EXIT_KEY;

public class AgentMessageHandler implements Runnable {
    private final Session session;
    private Client user;

    public AgentMessageHandler(Session session) {
        this.session = session;
    }

    public void setUser(Client user) {
        this.user = user;
    }

    @Override
    public void run() {
        while (user.isActive()) {
            String agentMessage = user.getUserMessage();
            if (agentMessage.startsWith(EXIT_KEY)) {
                agentMessage = "Agent leaved the conversation";
            }
            if (!agentMessage.isEmpty()) {
                session.getAsyncRemote().sendText(agentMessage);
            }

        }
    }
}
