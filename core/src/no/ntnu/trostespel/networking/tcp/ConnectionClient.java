package no.ntnu.trostespel.networking.tcp;

import com.google.gson.Gson;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.entity.Session;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static no.ntnu.trostespel.config.CommunicationConfig.CLIENT_UDP_GAMEDATA_PORT;

public class ConnectionClient {

    private static Gson gson = new Gson();

    public static Callable connect(InetAddress serverAddress, int serverPort) {
        return () -> doConnect(serverAddress, serverPort);
    }

    private static Response doConnect(InetAddress serverAddress, int serverPort) {
        Response data = null;
        DatagramSocket udpSocket = doCreateUDPSocket(CLIENT_UDP_GAMEDATA_PORT);
        if (udpSocket == null) {
            udpSocket = doCreateUDPSocket(0);
        }

        try {
            Socket socket = new Socket(serverAddress, serverPort);
            String username = Session.getInstance().getUsername();
            int sockPort = udpSocket.getLocalPort();
            String msg = gson.toJson(new TCPMessage(TCPEvent.CONNECT, new String[] {username, String.valueOf(sockPort)}));

            System.out.println("\r\nConnected to Server: " + socket.getInetAddress());

            // Print username and port to server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(msg);

            // Get answer from server
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String responseString = in.readLine();
            Response response = gson.fromJson(responseString, Response.class);
            response.setSocket(udpSocket);
            data = response;
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
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
