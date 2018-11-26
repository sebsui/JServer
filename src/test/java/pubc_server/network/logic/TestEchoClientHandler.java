package pubc_server.network.logic;

import ch.pubc.pubc_server.network.logic.NetworkLogic;

import java.io.Serializable;
import java.net.Socket;

public class TestEchoClientHandler extends NetworkLogic {

    public TestEchoClientHandler(Socket socket) {
        super(socket);
    }

    @Override
    protected void receivedObject(Serializable message) {
        sendObject(message);
    }

}
