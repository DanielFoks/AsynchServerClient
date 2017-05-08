package com.andersen.asynchronous.interfaces;


import java.io.IOException;
import java.util.List;

/**
 * General methods for working with the asynchronous server and the client.
 */
public interface AsynchronousClientServer {

    /**
     * Receives messages from client or server.
     *
     * @return Messages that were received. NULL if the messages were not received.
     * @throws IOException if message can not be received.
     */
    List<String> readMessages() throws IOException;

    /**
     * Sends message to client or server.
     *
     * @param message Message to be sent.
     * @return true if message was sent. False if was not.
     * @throws IOException if can not send the message.
     */
    boolean sendMessage(String message) throws InterruptedException;

    /**
     * Close server or client channel.
     *
     * @throws IOException if can not close channel.
     */
    void closeConnection() throws IOException, InterruptedException;
}
