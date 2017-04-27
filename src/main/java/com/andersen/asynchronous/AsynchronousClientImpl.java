package com.andersen.asynchronous;

import com.andersen.asynchronous.interfaces.AsynchronousClient;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Asynchronous client.
 */
public class AsynchronousClientImpl implements AsynchronousClient {

    /**
     * Clients SocketChannel.
     */
    private final SocketChannel clientChannel;

    /**
     * Log4j logger.
     */
    private static final Logger log = Logger.getLogger(AsynchronousClientImpl.class);

    /**
     * Buffer for send or receive message.
     */
    private ByteBuffer clientBuffer;

    public AsynchronousClientImpl(InetSocketAddress inetSocketAddress) {

        SocketChannel socketChannel = null;

        try {
            socketChannel = createConnection(inetSocketAddress);
        } catch (IOException e) {
            log.error("Can not create client SocketChannel: " + e.getMessage(), e);
        }

        clientChannel = socketChannel;
    }


    /**
     * Create clients SocketChannel.
     *
     * @param inetSocketAddress Address to connect.
     * @return Clients SocketChannel.
     * @throws IOException If can not connect to server.
     */
    @Override
    public SocketChannel createConnection(InetSocketAddress inetSocketAddress) throws IOException {
        clientBuffer = ByteBuffer.allocate(256);
        SocketChannel socketChannel = SocketChannel.open(inetSocketAddress);
        log.info("Client was connected to " + inetSocketAddress);
        return socketChannel;
    }

    /**
     * Receives message from server.
     *
     * @return Message that was received. NULL if the message was not received.
     * @throws IOException if message can not be received.
     */
    @Override
    public String readMessage() throws IOException {
        clientChannel.read(clientBuffer);
        String output = new String(clientBuffer.array()).trim();
        clientBuffer.flip();

        if (log.isDebugEnabled()) {
            log.debug("Message: \"" + output + "\"" + " was received");
        }

        return output;
    }

    /**
     * Sends message to server.
     *
     * @param message Message to be sent.
     * @return true if message was sent. False if was not.
     * @throws IOException          If can not send the message.
     * @throws InterruptedException If can not Thread.sleep.
     */
    @Override
    public boolean sendMessage(String message) throws InterruptedException {
        clientBuffer = ByteBuffer.wrap(message.getBytes());
        try {

            clientChannel.write(clientBuffer);
            clientBuffer.clear();
            if (log.isDebugEnabled()) {
                log.debug("Message: \"" + message + "\"" + " was sent");
            }
            Thread.sleep(1000);
            return true;
        } catch (IOException e) {
            log.error("Can not send the message: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Close client channel.
     *
     * @throws IOException If can not close channel.
     */
    @Override
    public void closeConnection() throws IOException {
        clientChannel.close();
        log.info("ClientChannel was closed.");
    }
}