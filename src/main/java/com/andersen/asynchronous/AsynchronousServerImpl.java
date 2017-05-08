package com.andersen.asynchronous;

import com.andersen.asynchronous.interfaces.AsynchronousServer;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;


/**
 * Asynchronous server.
 */
public class AsynchronousServerImpl extends Thread implements AsynchronousServer {

    /**
     * Servers  ServerSocketChannel
     */
    private final ServerSocketChannel serverChannel;

    /**
     * Clients channel.
     */
    private SelectionKey clientSelectionKey;

    /**
     * Server selector.
     */
    private Selector selector;

    /**
     * Check if you need to complete the thread.
     */
    private boolean close = false;

    /**
     * Log4j logger.
     */
    private static final Logger log = Logger.getLogger(AsynchronousServerImpl.class);

    /**
     * totalNumbersOfClients counts the number of clients that have been connected for the entire server lifetime.
     * connectedClients counts the number of clients that are connected now.
     */
    private int totalNumbersOfClients, connectedClients = 0;

    private BufferedReader bufferedReader;


    public AsynchronousServerImpl(InetSocketAddress inetSocketAddress) {

        ServerSocketChannel serverSocketChannel = null;

        try {
            serverSocketChannel = createConnection(inetSocketAddress);
        } catch (IOException e) {
            log.error("Can not create ServerSocketChannel: " + e.getMessage(), e);
        }

        serverChannel = serverSocketChannel;

    }


    @Override
    public void run() {

        while (!close) {

            int readyChannels = 0;
            try {
                readyChannels = selector.select();
                log.debug("Ready channels: " + readyChannels);
            } catch (IOException e) {
                log.error("Selector troubles: " + e.getMessage(), e);
            }

            if (readyChannels == 0) continue;

            Set<SelectionKey> selectedKeys = selector.selectedKeys();

            Iterator<SelectionKey> iterator = selectedKeys.iterator();

            while (iterator.hasNext()) {
                clientSelectionKey = iterator.next();

                if (clientSelectionKey.isAcceptable()) {
                    try {
                        connectClient();
                        log.info("Total number of clients: " + ++totalNumbersOfClients);
                        log.info("Connected clients: " + ++connectedClients);
                    } catch (IOException e) {
                        log.error("Can not connect client: " + e.getMessage(), e);
                    }
                } else if (clientSelectionKey.isReadable()) {
                    try {
                        List<String> messages = readMessages();
                        if (readMessages() != null) {
                            inner:
                            for (String message : messages) {

                                if (message.equals("exit")) {
                                    closeClientConnection();
                                    log.info("Connected clients: " + --connectedClients);
                                    break inner;

                                } else {

                                    sendMessage(message.toUpperCase());

                                }

                            }
                        }
                    } catch (IOException e) {
                        log.error("Can not read the message: " + e.getMessage(), e);
                    }
                }
                try {
                    iterator.remove();
                } catch (ConcurrentModificationException e) {
                    System.out.println("LOOOOOOOOOOOOOOOOOL");
                }

            }
        }
    }


    /**
     * Create ServerSocketChannel.
     *
     * @param inetSocketAddress Address to connect.
     * @return ServerSocketChannel.
     * @throws IOException If can not create ServerSocketChannel.
     */
    @Override
    public ServerSocketChannel createConnection(InetSocketAddress inetSocketAddress) throws IOException {
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(inetSocketAddress);
        serverSocket.configureBlocking(false);
        int ops = serverSocket.validOps();
        selector = Selector.open();
        serverSocket.register(selector, ops, null);

        log.info("Server was started.");

        return serverSocket;
    }

    /**
     * Connects the client to the server.
     *
     * @throws IOException If can not connect the client.
     */
    @Override
    public void connectClient() throws IOException {
        SocketChannel client = serverChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        log.info("New client (" + client.getLocalAddress() + " | " + client.getRemoteAddress() + ") was connected.");
    }


    /**
     * Close client connection.
     *
     * @throws IOException If can not close client SocketChannel.
     */
    @Override
    public void closeClientConnection() throws IOException {
        SocketChannel client = (SocketChannel) clientSelectionKey.channel();
        SocketAddress local = client.getLocalAddress();
        SocketAddress remote = client.getRemoteAddress();
        client.close();
        log.info("Client (" + local + " | " + remote + ") was disconnected.");
    }


    /**
     * Receives messages from client.
     *
     * @return Messages that were received. NULL if the messages were not received.
     * @throws IOException if messages can not be received.
     */
    @Override
    public List<String> readMessages() throws IOException {
        ArrayList<String> messages = null;
        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
        SocketChannel client = (SocketChannel) clientSelectionKey.channel();
        client.read(byteBuffer);

        String output = new String(byteBuffer.array(), "UTF-8");

        int beginMessage = 0;

        if (output.length() > 0) {
            messages = new ArrayList<>();
            for (int i = 0; i < output.length(); i++) {
                char nl = output.charAt(i);
                if (nl == 10) {
                    messages.add(output.substring(beginMessage, i));
                    beginMessage = i + 1;
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Message: \"" + output + "\"" + " was received");
        }

        return messages;
    }

    /**
     * Sends message to client.
     *
     * @param message Message to be sent.
     * @return true if message was sent. False if was not.
     * @throws IOException if can not send the message.
     */
    @Override
    public boolean sendMessage(String message) {
        message += "\n";
        SocketChannel client = (SocketChannel) clientSelectionKey.channel();
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        try {
            client.write(buffer);
            if (log.isDebugEnabled()) {
                log.debug("Message: \"" + message + "\"" + " was sent");
            }
            return true;
        } catch (IOException e) {
            log.error("Can not send the message: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Close server channel.
     *
     * @throws IOException if can not close channel.
     */
    @Override
    public void closeConnection() throws IOException {
        int t;

        do {

            t = selector.wakeup().selectNow();

        } while (t != 0);

        close = true;

        selector.close();
        serverChannel.close();

        log.info("Server was closed.");
    }

    public int getTotalNumbersOfClients() {
        return totalNumbersOfClients;
    }
}