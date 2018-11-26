package ch.pubc.pubc_server.server;

import ch.pubc.pubc_server.exception.NotSupportedException;
import ch.pubc.pubc_server.network.logic.NetworkLogic;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

/**
 * Basis Struktur zum erstellen eines neuen Spielservers.Erstellt die verschiedenen Threads zur Kommunikation mit den Clients.
 *
 * @param <T> Typ der Clienthandler Klasse.
 *            Die Clienthandler Klasse muss auf die Antworten des Clients reagieren können.
 */
public abstract class MultiThreadedServer<T extends NetworkLogic> implements Runnable {

    protected int serverPort;
    protected ServerSocket serverSocket = null;
    protected boolean isStopped = false;
    protected ExecutorService threadPool;
    protected int amountThreads;
    private Function<Socket, T> supplierClientHandler;
    protected List<T> clientThreads;

    /**
     * Nicht erlaubt. Es werden immer Infromationen für die Connection benötigt.
     *
     * @throws NotSupportedException
     */
    private MultiThreadedServer() throws NotSupportedException {
        throw new NotSupportedException();
    }

    /**
     * @param port                  Auf diesem Port wird das Spiel durchgeführt. Jedes Spiel benötigt einen eigenen Port.
     * @param amountThreads         Entspricht auch der Anzahl an möglicne Spieler aka CLients.
     * @param supplierClientHandler Factory zum erstellen von neuen ClientHandler methoden.
     */
    public MultiThreadedServer(int port, int amountThreads, Function<Socket, T> supplierClientHandler) {
        this.serverPort = port;
        this.amountThreads = amountThreads;
        this.supplierClientHandler = supplierClientHandler;
        threadPool = Executors.newFixedThreadPool(amountThreads);
        clientThreads = new ArrayList<>();
    }

    /**
     * Nicht aufrufen, wird vom Thread selbst gestartet.
     * Startet so lang nicht gestoppt den loop zum entgegennehmen von Clientrequests.
     */
    @Override
    public void run() {
        openServerSocket();
        while (!isStopped()) {
            handleConnectionRequest();
        }
        this.threadPool.shutdown();
    }

    /**
     * Handelt die COnnection zum Client. Verifiziert auch, dass sich nicht zu viele Spieler Connecten
     */
    private void handleConnectionRequest() {
        try {
            Socket partnerSocket = serverSocket.accept();
            System.out.println("Connected");
            if (clientThreads.size() < amountThreads) {
                acceptConnection(partnerSocket);
            } else {
                handleFullAmountThreads(partnerSocket);
            }
        } catch (IOException e) {
            if (!isStopped()) {
                e.printStackTrace();
            }
            //if isStopped is true, a ioexception( from the socket) is normal, as the socket accept ist interrupted.
        }
    }

    /**
     * Enthält die Logik, welche zum Zug kommt, wenn alle Spielplätze schon gesetzt sind.
     *
     * @param partnerSocket
     * @throws IOException
     */
    private void handleFullAmountThreads(Socket partnerSocket) throws IOException {
        Optional<T> optionalOldThread = getOldConnection(partnerSocket);
        if (optionalOldThread.isPresent()) {
            optionalOldThread.get().stop();
            clientThreads.remove(optionalOldThread.get());
            T clientThread = supplierClientHandler.apply(partnerSocket);
            threadPool.execute(clientThread);
            clientThreads.add(clientThread);
            clientChanged(optionalOldThread.get(),clientThread);
        } else {
            partnerSocket.close();
        }
    }

    /**
     * Sucht eine bestehende Connection auf basis des Socket Hostname. Es werden nur inactive Conenctions angezeigt.
     *
     * @param partnerSocket
     * @return
     */
    private Optional<T> getOldConnection(Socket partnerSocket) {
        return clientThreads.stream()
                .filter(t -> t.getPartnerIdentification().equals(partnerSocket.getInetAddress().getHostName()))
                .findAny();
    }

    /**
     * Bestätigt die clientconnection und lagert den Thread ind en Pool.
     *
     * @param partnerSocket
     */
    private void acceptConnection(Socket partnerSocket) {
        T clientThread = supplierClientHandler.apply(partnerSocket);
        threadPool.execute(clientThread);
        clientThreads.add(clientThread);
        newPlayerAdded(clientThread);
    }


    private synchronized boolean isStopped() {
        return isStopped;
    }

    /**
     * Beendet den Server und alle Clientconnections.
     */
    public synchronized void stop() {
        isStopped = true;
        try {
            clientThreads.forEach(T::stop);
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    /**
     * Eröffent einen neuen Serversocket auf Basis des Ports aus dem Constructor
     */
    private void openServerSocket() {
        try {
            serverSocket = new ServerSocket(serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port", e);
        }
    }

    /**
     * Broadcast an alle Clients, welche mit dem Server verbunden sind.
     *
     * @param message
     */
    public void sendMessageToAllClients(Serializable message) {
        clientThreads.stream().forEach(networkLogic -> networkLogic.sendObject(message));
    }

    /**
     * Zählt die Anzahl aktiver Clients.
     *
     * @param active
     * @return
     */
    public int getAmountClients(boolean active) {
        return (int) clientThreads.stream().filter(t -> t.isActive() == active).count();
    }

    public abstract void newPlayerAdded(T clientThread);
    public abstract void playerRemoved(T clientThread);
    public abstract void clientChanged(T old, T newClient);
}