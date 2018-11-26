package pubc_server.network.logic;

import ch.pubc.pubc_server.network.logic.NetworkLogic;

import java.io.IOException;
import java.io.Serializable;

public class TestGameClient extends NetworkLogic {

    public TestGameClient(int portNumber, String host) throws IOException {
        super(portNumber, host);
    }

    public Object response;

    @Override
    protected void receivedObject(Serializable message) {
        response = message;
    }

}
