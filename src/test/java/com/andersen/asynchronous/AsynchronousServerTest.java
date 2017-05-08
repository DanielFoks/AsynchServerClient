package com.andersen.asynchronous;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsynchronousServerTest {

    AsynchronousServerImpl server;

    @Before
    public void startServer() {
        server = new AsynchronousServerImpl(new InetSocketAddress("localhost", 32454));
        server.start();
    }

    @Test
    public void testAsynchronousServer() throws IOException {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        char[] alphabet = new char[25];

        int count = 0;

        for (char j = 'a'; j < 'z'; j++) {
            alphabet[count] = j;
            count++;
        }


        for (int i = 0; i < 1000; i++) {

            executor.execute(() -> {

                AsynchronousClientImpl asynchronousClient = new AsynchronousClientImpl(new InetSocketAddress("localhost", 32454));

                try {

                    Random random = new Random();

                    String[] clientsMessages = new String[random.nextInt(5)];

                    for (int j = 0; j < clientsMessages.length; j++) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int k = 0; k < random.nextInt(200); k++) {
                            stringBuilder.append(alphabet[random.nextInt(25)]);
                        }
                        clientsMessages[j] = stringBuilder.toString();
                    }

                    for (int j = 0; j < clientsMessages.length; j++) {
                        asynchronousClient.sendMessage(clientsMessages[j]);
                        Assert.assertEquals(asynchronousClient.readMessages().get(0), clientsMessages[j].toUpperCase());
                    }

                    asynchronousClient.closeConnection();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            });
        }

        executor.shutdown();

        while (!executor.isTerminated()) {
        }

        server.closeConnection();
        Assert.assertEquals(1000, server.getTotalNumbersOfClients());
    }

}