package no.ntnu.TrosteSpel;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NetworkController {

    private Socket udpSocket;
    private Socket tcpSocket;

    private PrintWriter out;
    private BufferedReader in;

    private static NetworkController instance = null;

    public static NetworkController getInstance()
    {
        if (instance == null)
            instance = new NetworkController();
        return instance;
    }

    public void startUdpConnection() throws IOException {
        udpSocket = new Socket(Game.SERVER_ADDRESS, Game.SERVER_UDP_PORT);

    }

    public void startTcpConnection() throws IOException {
        tcpSocket = new Socket(Game.SERVER_ADDRESS, Game.SERVER_TCP_PORT);
        out = new PrintWriter(tcpSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));

    }
}

