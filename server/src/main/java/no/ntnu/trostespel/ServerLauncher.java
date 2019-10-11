package no.ntnu.trostespel;

import no.ntnu.trostespel.config.ConnectionConfig;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerLauncher {

    public static void main(String[] args) throws Exception {
        // Initialize ConnectionConfig
        ConnectionConfig.getInstance();

        UDPServer UDPClient = new UDPServer(ConnectionConfig.SERVER_UDP_GAMEDATA_RECEIVE_PORT);
        TCPServer TCPClient = new TCPServer(ConnectionConfig.SERVER_TCP_CONNECTION_RECEIVE_PORT);

        Thread t1 = new Thread(UDPClient);
        Thread t2 = new Thread(TCPClient);
        t1.setName("TCPClient");
        t2.setName("UDPClient");
        t1.start();
        t2.start();

        new GameServer();
    }
}