package com.besomhead.touchsoft;

import com.besomhead.touchsoft.model.Client;
import com.besomhead.touchsoft.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.Socket;

import static com.besomhead.touchsoft.ConsoleChatServer.*;

@ServerEndpoint("/chat")
public class WebSocketEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEndpoint.class);
    private Client client;
    private AgentMessageHandler agentMessageHandler;


    @OnOpen
    public void onOpen(Session session) {
        agentMessageHandler = new AgentMessageHandler(session);
        LOGGER.info("Open Connection with client ");
    }

    @OnClose
    public void onClose() {
        client.sendMessageToUser(EXIT_KEY);
        client.closeConnection();
        LOGGER.info("Close Connection with client " + client.getName());
    }

    @OnMessage
    public void onMessage(String message) {
        if (message.startsWith(REGISTER_KEY)) {
            String name = message.substring(message.lastIndexOf(" "));
            try {
                client = new Client(name, new User(new Socket(SERVER_HOST, SERVER_PORT)));
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage());
            }
            agentMessageHandler.setClient(client);
            new Thread(agentMessageHandler).start();
            client.sendMessageToUser(message);
        }
        client.sendMessageToUser(message);
        LOGGER.info("Message from the client: " + message);
    }

    @OnError
    public void onError(Throwable ex) {
        LOGGER.error(ex.getMessage());
        onClose();
    }

}
