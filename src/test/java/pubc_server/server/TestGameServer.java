package pubc_server.server;

import ch.pubc.pubc_server.server.MultiThreadedServer;
import pubc_server.network.logic.TestEchoClientHandler;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Function;

public class TestGameServer {

    private MultiThreadedServer<TestEchoClientHandler> server;
    Function<Socket, TestEchoClientHandler> clientHandlerProvider = TestEchoClientHandler::new;
    Thread thread;

    public TestGameServer(int port, int amountPlayer) throws IOException {
        server = new MultiThreadedServer<TestEchoClientHandler>(port, amountPlayer, clientHandlerProvider) {
            @Override
            public void newPlayerAdded(TestEchoClientHandler clientThread) {}
        };
        thread = new Thread(server);
        thread.start();
    }

    public void stopGameServer() throws InterruptedException {
        server.stop();
        thread.join();
    }

    public int getAmountClients(boolean activ) {
        return server.getAmountClients(activ);
    }

    public void sendMessageToAllClients(Serializable message) {
        server.sendMessageToAllClients(message);
    }

}
