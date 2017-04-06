import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AsynchClient {

    public void go()
            throws IOException, InterruptedException, ExecutionException {

        AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        InetSocketAddress hostAddress = new InetSocketAddress("localhost", 3883);
        Future future = client.connect(hostAddress);
        future.get();

        System.out.println("Client is started");
        System.out.println("Sending messages to server: ");

        String [] messages = new String [] {"Hello", "World", "Bye."};

        for (int i = 0; i < messages.length; i++) {

            byte [] message = new String(messages [i]).getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(message);
            Future result = client.write(buffer);

            while (! result.isDone()) {
                System.out.println("...");
            }

            System.out.println(messages [i]);
            buffer.clear();
            Thread.sleep(2000);
        }

        /*client.close();*/
    }
}
