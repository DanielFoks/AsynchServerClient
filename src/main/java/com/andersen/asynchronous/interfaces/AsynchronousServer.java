package com.andersen.asynchronous.interfaces;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

/**
 * Server interface.
 */
public interface AsynchronousServer extends AsynchronousClientServer {

    /**
     * Create ServerSocketChannel.
     *
     * @param inetSocketAddress Address to connect.
     * @return ServerSocketChannel.
     * @throws IOException If can not create ServerSocketChannel.
     */
    ServerSocketChannel createConnection(InetSocketAddress inetSocketAddress) throws IOException;

    /**
     * Connects the client to the server.
     *
     * @throws IOException If can not connect the client.
     */
    void connectClient() throws IOException;

    /**
     * Close client connection.
     *
     * @throws IOException If can not close client SocketChannel.
     */
    void closeClientConnection() throws IOException;
}
