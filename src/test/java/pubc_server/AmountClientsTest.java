package pubc_server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pubc_server.network.logic.TestGameClient;
import pubc_server.server.TestGameServer;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

public class AmountClientsTest {

    public static final int PORT_NUMBER = 2;
    public static final int AMOUNT_PLAYER = 2;
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
    public void testLessClients() throws IOException, InterruptedException {
        startNClients(AMOUNT_PLAYER - 1);
        assertEquals(AMOUNT_PLAYER - 1, testGameServer.getAmountClients(true));
    }

    @Test
    public void testRightAmountClients() throws IOException, InterruptedException {
        startNClients(AMOUNT_PLAYER);
        assertEquals(AMOUNT_PLAYER, testGameServer.getAmountClients(true));
    }

    @Test
    public void shouldRefuseLastClient() throws IOException, InterruptedException {
        startNClients(AMOUNT_PLAYER + 1);
        assertEquals(AMOUNT_PLAYER, testGameServer.getAmountClients(true));
    }


    private void startNClients(int n) throws IOException, InterruptedException {
        while (n != 0) {
            new TestGameClient(PORT_NUMBER, "localhost").run();
            n--;
            Thread.sleep(100);
        }
        Thread.sleep(100);
    }
}
