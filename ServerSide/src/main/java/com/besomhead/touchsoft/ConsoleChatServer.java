package com.besomhead.touchsoft;

import com.besomhead.touchsoft.model.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConsoleChatServer {
    public static final String REGISTER_KEY = "/register";
    public static final String EXIT_KEY = "/exit";
    public static final String LEAVE_KEY = "/leave";
    public static final String USER_TYPE_CLIENT = "client";
    public static final String USER_TYPE_AGENT = "agent";

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleChatServer.class);
    private static final ExecutorService serverThreadsExecutor = Executors.newFixedThreadPool(20);
    private static final BlockingQueue<Agent> agentsQueue = new ArrayBlockingQueue<>(10);

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(9876)) {
            LOGGER.info("Chat server started");
            while (!serverSocket.isClosed()) {
                serverThreadsExecutor.execute(new ChatServerThread(serverSocket.accept()));
            }

            serverThreadsExecutor.shutdown();
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        }

        LOGGER.info("Chat server shut down");
    }

    public static void saveAgent(Agent agent) {
        try {
            agentsQueue.put(agent);
            LOGGER.info("Agent " + agent.getName() + " is vacant");
        } catch (InterruptedException ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    public static Agent getVacantAgent() {
        Agent vacantAgent = null;

        try {
            vacantAgent = agentsQueue.take();
            LOGGER.info("Agent " + vacantAgent.getName() + " is busy");
        } catch (InterruptedException ex) {
            LOGGER.error(ex.getMessage());
        }

        return vacantAgent;
    }

    public static void removeAgent(Agent agent) {
        agentsQueue.remove(agent);
        LOGGER.info("Agent " + agent.getName() + " removed from the system");
    }
}
