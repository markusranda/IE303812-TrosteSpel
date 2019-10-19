package no.ntnu.trostespel.networking;

import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.entity.Session;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ConnectionClient {


    public static Callable connect(InetAddress serverAddress, int serverPort) {
        return () -> doConnect(serverAddress, serverPort);
    }

    private static String doConnect(InetAddress serverAddress, int serverPort) {
        String data = null;
        try {
            Socket socket = new Socket(serverAddress, serverPort);
            String username = Session.getInstance().getUsername();

            System.out.println("\r\nConnected to Server: " + socket.getInetAddress());

            // Print username to server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(username);

            // Get answer from server
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            data = in.readLine();

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
}
