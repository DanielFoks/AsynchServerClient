package com.andersen.asynchronous.interfaces;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * Client interface.
 */
public interface AsynchronousClient extends AsynchronousClientServer {

    /**
     * Create clients SocketChannel.
     *
     * @param inetSocketAddress Address to connect.
     * @return Clients SocketChannel.
     * @throws IOException If can not connect to server.
     */
    SocketChannel createConnection(InetSocketAddress inetSocketAddress) throws IOException;

}
