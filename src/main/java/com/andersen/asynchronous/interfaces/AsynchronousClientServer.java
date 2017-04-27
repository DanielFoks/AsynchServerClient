package com.andersen.asynchronous.interfaces;


import java.io.IOException;

/**
 * General methods for working with the asynchronous server and the client.
 */
public interface AsynchronousClientServer {

    /**
     * Receives message from client or server.
     *
     * @return Message that was received. NULL if the message was not received.
     * @throws IOException if message can not be received.
     */
    String readMessage() throws IOException;

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
    void closeConnection() throws IOException;
}
