package pubc_server.network;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pubc_server.network.logic.TestGameClient;
import pubc_server.server.TestGameServer;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

public class SendMessageToAllTest {

    public static final int PORT_NUMBER = 2;
    public static final int AMOUNT_PLAYER = 2;
    public static final String HELLO_FROM_SERVER = "Hello from Server";
    public static final int CONNECTION_TIME = 500;
    TestGameServer testGameServer;

    @Before
    public void setupServer() throws IOException {
        testGameServer = new TestGameServer(PORT_NUMBER, AMOUNT_PLAYER);
    }

    @After
    public void closeServer() throws InterruptedException {
        testGameServer.stopGameServer();
    }

    @Test
    public void bothShouldGetTheMessage() throws IOException, InterruptedException {
        TestGameClient testGameClient1 = new TestGameClient(PORT_NUMBER, "localhost");
        testGameClient1.run();
        Thread.sleep(CONNECTION_TIME);
        TestGameClient testGameClient2 = new TestGameClient(PORT_NUMBER, "localhost");
        testGameClient2.run();
        Thread.sleep(CONNECTION_TIME);
        testGameServer.sendMessageToAllClients(HELLO_FROM_SERVER);
        Thread.sleep(CONNECTION_TIME);
        assertEquals(HELLO_FROM_SERVER, testGameClient1.response);
        assertEquals(HELLO_FROM_SERVER, testGameClient2.response);
    }

}
