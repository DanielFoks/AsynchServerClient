import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


public class AsynchTest {

    @Test
    public void oneThousandClientsTest() throws InterruptedException, ExecutionException, IOException {
        for (int i = 0; i < 1000; i++) {
            new AsynchClient().go();
        }
    }
}
