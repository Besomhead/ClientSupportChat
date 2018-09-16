package com.besomhead.touchsoft;

import com.besomhead.touchsoft.model.Client;

import javax.websocket.Session;

import static com.besomhead.touchsoft.ConsoleChatServer.EXIT_KEY;

public class AgentMessageHandler implements Runnable {
    private final Session session;
    private Client client;

    public AgentMessageHandler(Session session) {
        this.session = session;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        while (client.isActive()) {
            String agentMessage = client.getUserMessage();
            if (agentMessage.startsWith(EXIT_KEY)) {
                agentMessage = "Agent leaved the conversation";
            }
            if (!agentMessage.isEmpty()) {
                session.getAsyncRemote().sendText(agentMessage);
            }

        }
    }
}
