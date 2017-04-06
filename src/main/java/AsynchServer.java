import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class AsynchServer extends Thread{

    private AsynchronousSocketChannel clientChannel;
    private static int countOfClients;

    public AsynchServer(AsynchronousSocketChannel clientChannel){
        super();
        this.clientChannel = clientChannel;

        setDaemon(true);
        start();
    }

    @Override
    public void run() {

        System.out.println("Client № "+countOfClients+" connected");

            while (true) {

                ByteBuffer buffer = ByteBuffer.allocate(32);
                Future result = clientChannel.read(buffer);

                while (! result.isDone()) {}

                buffer.flip();
                String message = new String(buffer.array()).trim();
                System.out.println(message);

                if (message.equals("Bye.")) {
                    break;
                }

                buffer.clear();

            }

            try {
                clientChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open();
        InetSocketAddress hostAddress = new InetSocketAddress("localhost", 3883);
        serverChannel.bind(hostAddress);

        System.out.println("Waiting for client to connect... ");

        Future acceptResult;
        countOfClients = 0;

        while (true){
            acceptResult = serverChannel.accept();
            AsynchronousSocketChannel clientChannel = (AsynchronousSocketChannel) acceptResult.get();
            if ((clientChannel != null) && (clientChannel.isOpen())) {
                countOfClients++;
                new AsynchServer(clientChannel);
            }
        }
    }
}
