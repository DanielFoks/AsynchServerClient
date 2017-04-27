package com.andersen.asynchronous;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsynchronousServerTest {

    AsynchronousServerImpl server;

    @Before
    public void startServer(){
        server = new AsynchronousServerImpl(new InetSocketAddress("localhost", 32454));
        server.start();
    }

    @Test
    public void testAsynchronousServer() {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 1000; i++) {

            executor.execute(() -> {

                AsynchronousClientImpl asynchronousClient = new AsynchronousClientImpl(new InetSocketAddress("localhost", 32454));

                try {
                    asynchronousClient.sendMessage("Hello");
                    Assert.assertEquals("Hello", asynchronousClient.readMessage());
                    asynchronousClient.sendMessage("World");
                    Assert.assertEquals("World", asynchronousClient.readMessage());
                    asynchronousClient.sendMessage("Exit");
                    Assert.assertEquals("Exit", asynchronousClient.readMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    asynchronousClient.closeConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        }

        executor.shutdown();

        while (!executor.isTerminated()) {
        }

        Assert.assertEquals(1000,server.getTotalNumbersOfClients());
    }

    @After
    public void closeServerConnection() throws IOException {
        server.closeConnection();
    }


}
