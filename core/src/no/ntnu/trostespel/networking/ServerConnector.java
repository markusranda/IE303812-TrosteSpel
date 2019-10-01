package no.ntnu.trostespel.networking;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerConnector {

    public ServerConnector() throws Exception {
        TCPClient client = new TCPClient(
                InetAddress.getByName("192.168.50.50"),
                Integer.parseInt("7083"));

        Thread t = new Thread(client);
        t.start();

    }
}
