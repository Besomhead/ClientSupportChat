package com.besomhead.touchsoft;

import com.besomhead.touchsoft.model.Client;
import com.besomhead.touchsoft.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.Socket;

import static com.besomhead.touchsoft.ConsoleChatServer.EXIT_KEY;
import static com.besomhead.touchsoft.ConsoleChatServer.REGISTER_KEY;

@ServerEndpoint("/chat")
public class WebSocketEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEndpoint.class);
    private Client client;


    @OnOpen
    public void onOpen() {
        LOGGER.info("Open Connection with client ");
    }

    @OnClose
    public void onClose() {
        client.sendMessageToUser(EXIT_KEY);
        client.closeConnection();
        LOGGER.info("Close Connection with client " + client.getName());
    }

    @OnMessage
    public String onMessage(String message) {
        if (message.startsWith(REGISTER_KEY)) {
            String name = message.substring(message.lastIndexOf(" "));
            try {
                client = new Client(name, new User(new Socket("localhost", 9876)));
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage());
            }
            client.sendMessageToUser(message);
            return "";
        }
        client.sendMessageToUser(message);
        LOGGER.info("Message from the client: " + message);
//        String echoMsg = client.getUserMessage();
//        return echoMsg;
        return "stub answer!";
//        return client.getUserMessage();
    }

    @OnError
    public void onError(Throwable ex) {
        LOGGER.error(ex.getMessage());
        onClose();
    }

}
