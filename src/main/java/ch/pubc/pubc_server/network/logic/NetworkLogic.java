package ch.pubc.pubc_server.network.logic;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

import ch.pubc.pubc_server.exception.NotSupportedException;

/**
 * Abstrakte Basisclasse zum bereitstellen der Kommunikationslogik.
 *
 * @param <T>
 */
public abstract class NetworkLogic<T extends Serializable> extends NetworkPartner implements Runnable {

    protected Socket socket;
    private String partnerIdentification;

    /**
     * Nicht erlaubt. Es werden immer Infromationen für die Connection benötigt.
     *
     * @throws NotSupportedException
     */
    private NetworkLogic() throws NotSupportedException {
        throw new NotSupportedException();
    }

    /**
     * Construktor zum eröffnen eines neuen Sockets. Wird vom Client zum Connecten zum Server verwendet.
     *
     * @param portNumber
     * @param host
     * @throws IOException
     */
    public NetworkLogic(int portNumber, String host) throws IOException {
        socket = new Socket(host, portNumber);
        partnerIdentification = socket.getInetAddress().getHostName();
        System.out.println(partnerIdentification);
    }

    /**
     * Construktor zum eröffnen eines neuen Sockets. Wird vom Server zum Connecten zum Client verwendet.
     *
     * @param socket
     */
    public NetworkLogic(Socket socket) {
        this.socket = socket;
        partnerIdentification = socket.getInetAddress().getHostName();
        System.out.println(partnerIdentification);
    }

    /**
     * Abstractre MEthode, welche angiebt, wie erhaltene Objekte zu behandeln sind.
     *
     * @param message
     */
    protected abstract void receivedObject(T message);


    /**
     * Methode zum senden von Objekten an den Empfänger. Wenn die Nachricht nicht zugestellt werden kann, wird die verbindung beendet.
     * @param message
     */
    public void sendObject(T message) {
        new Thread(() -> super.sendObject(message, socket)).start();
    }

    @Override
    public void run() {
        Consumer<T> consumer = this::receivedObject;
        listen(socket, consumer);
    }

    /**
     * @return hostname des anderen Sockets zurück.
     */
    public String getPartnerIdentification() {
        return partnerIdentification;
    }

    /**
     * @return port des eigenen sockets.
     */
    public int getPort() {
        return socket.getLocalPort();
    }
}
