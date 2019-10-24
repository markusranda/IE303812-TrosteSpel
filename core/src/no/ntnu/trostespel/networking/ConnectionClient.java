package no.ntnu.trostespel.networking;

import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.entity.Session;

import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static no.ntnu.trostespel.config.CommunicationConfig.CLIENT_UDP_GAMEDATA_RECEIVE_PORT;

public class ConnectionClient {


    public static Callable connect(InetAddress serverAddress, int serverPort) {
        return () -> doConnect(serverAddress, serverPort);
    }

    private static Response doConnect(InetAddress serverAddress, int serverPort) {
        Response data = null;
        DatagramSocket udpSocket = doCreateUDPSocket(CLIENT_UDP_GAMEDATA_RECEIVE_PORT);
        if (udpSocket == null) {
            udpSocket = doCreateUDPSocket(0);
        }

        try {
            Socket socket = new Socket(serverAddress, serverPort);
            String msg = Session.getInstance().getUsername() + " " + udpSocket.getLocalPort();

            System.out.println("\r\nConnected to Server: " + socket.getInetAddress());

            // Print username and port to server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(msg);

            // Get answer from server
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            long pid = Long.parseLong(in.readLine());

            data = new Response(udpSocket, Session.getInstance().getUsername(), pid);

            socket.close();
        } catch (Exception e) {
            System.out.println("Connection to server failed... Trying again in "
                    + CommunicationConfig.RETRY_CONNECTION_TIMEOUT / 1000 + " seconds");
            CountDownLatch lock = new CountDownLatch(1);
            try {
                lock.await(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            doConnect(serverAddress, serverPort);
        }
        return data;
    }

    private static DatagramSocket doCreateUDPSocket(int port) {
        DatagramSocket udpSocket = null;
        try {
            if (port == 0) {
                udpSocket = new DatagramSocket();
            } else {
                udpSocket = new DatagramSocket(port);
            }
        } catch (SocketException e) {
            System.out.println("Couldnt bind UDP socket to selected port . . .");
            return null;
        }
        return udpSocket;
    }
}
