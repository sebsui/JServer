package ch.pubc.pubc_server.network.logic;

import ch.pubc.pubc_server.network.serialization.SerializationHelper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * Abstracte Classe zum bereitstellen der Funktionalität zum senden/empfangen der Daten.
 * Übernimmt das Serialisieren / Deserialisieren der payload.
 */
public abstract class NetworkPartner {

    protected boolean isActive = true;

    /**
     * Sendet ein Serialisierbares Objekt an den Kommunikationspartner.
     * Das Objekt wird automatisch Serialisiert.
     *
     * @param payload  Zu sendendes Obejkt
     * @param receiver Socket des Empfängers
     * @throws IOException
     */
    public void sendObject(Serializable payload, Socket receiver) {
        try {
            byte[] serialized = SerializationHelper.serialize(payload);
            DataOutputStream outPutStream = new DataOutputStream(receiver.getOutputStream());
            outPutStream.writeInt(serialized.length);
            outPutStream.write(serialized);
            outPutStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            isActive = false;
        }
    }

    /**
     * Startet einen Thread für die gelieferte Methode zum bearbeiten der eingegangenen Messages.
     *
     * @param socket   Eigener Socket wo die Daten empfangen werden
     * @param consumer Methode zum bearbeiten der erhaltenen Objekte
     */
    public void listen(Socket socket, Consumer consumer) {
        (new Thread(() -> listenToSocket(socket, consumer))).start();
    }

    /**
     * Empfängt die erhaltenen Bytes und erstellt das Obejkt daraus. Ruft zum Abschluss die gelieferte Methode zum bearbeiten der Objekte auf.
     *
     * @param socket   Eigener Socket wo die Daten empfangen werden
     * @param consumer Methode zum bearbeiten der erhaltenen Objekte
     */
    private void listenToSocket(Socket socket, Consumer consumer) {
        try {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            while (isActive) {
                int length = dataInputStream.readInt();
                if (length > 0) {
                    byte[] serialized = new byte[length];
                    dataInputStream.readFully(serialized, 0, serialized.length);
                    Serializable networkPayload = SerializationHelper.deserialize(serialized);
                    consumer.accept(networkPayload);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isActive() {
        return isActive;
    }

    /**
     * Beendet den listener
     */
    public void stop() {
        isActive = false;
    }
}
