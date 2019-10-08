package no.ntnu.trostespel;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws Exception {
        UDPServer UDPClient = new UDPServer(7080);
        TCPServer TCPClient = new TCPServer(7083);

        Thread t1 = new Thread(UDPClient);
        Thread t2 = new Thread(TCPClient);
        t1.setName("TCPClient");
        t2.setName("UDPClient");
        t1.start();
        t2.start();

        retrieveAndSend();
    }

    private static void retrieveAndSend() {
        // TODO: 08.10.2019 Add the retrieving and sending of Game State implementation here
    }
}