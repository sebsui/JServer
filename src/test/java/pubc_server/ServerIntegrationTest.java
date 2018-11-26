package pubc_server;

import pubc_server.network.logic.TestGameClient;
import pubc_server.server.TestGameServer;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

public class ServerIntegrationTest {

    public static final String PAYLOAD = "Test";
    TestGameServer testGameServer = new TestGameServer(1, 10);

    public ServerIntegrationTest() throws IOException {
    }

    public void testCaseIntegrationEchoClient() throws IOException, InterruptedException {
        TestGameClient testGameClient = new TestGameClient(1, "localhost");
        testGameClient.run();
        testGameClient.sendObject(PAYLOAD);
        Thread.sleep(1000);
        assertEquals(PAYLOAD, testGameClient.response);
    }
}