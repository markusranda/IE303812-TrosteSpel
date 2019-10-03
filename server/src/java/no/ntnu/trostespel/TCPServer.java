package no.ntnu.trostespel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer implements Runnable {

    private ServerSocket server;

    public TCPServer(int port) throws Exception {
        this.server = new ServerSocket(port, 1, null);
    }

    @Override
    public void run() {
        try {
            String data = null;
            Socket client = this.server.accept();
            String clientAddress = client.getInetAddress().getHostAddress();
            System.out.println("\r\nNew TCP connection from " + clientAddress);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(client.getInputStream()));
            while ((data = in.readLine()) != null) {
                System.out.println("\r\nMessage from " + clientAddress + ": " + data);
            }
            client.close();
        } catch (IOException io) {
            run();
        }
        run();
    }

    public InetAddress getSocketAddress() {
        return this.server.getInetAddress();
    }

    public int getPort() {
        return this.server.getLocalPort();
    }
}