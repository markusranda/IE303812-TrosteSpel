package no.ntnu.trostespel;

import no.ntnu.trostespel.model.Connection;
import no.ntnu.trostespel.model.Connections;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


// TODO: 11.10.2019 This class should contain some sort of threadpool with a fixed size to handle all connection requests.

public class ConnectionManager implements Runnable {

    private ServerSocket server;

    public ConnectionManager(int port) throws Exception {
        this.server = new ServerSocket(port, 1, null);
    }

    @Override
    public void run() {
        try {
            String data = null;
            Socket client = this.server.accept();
            String clientAddress = client.getInetAddress().getHostAddress();
            System.out.println("\r\nNew TCP connection from " + clientAddress);

            // Receive message from client
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            data = in.readLine();
            System.out.println(data);

            // Send the response back to the client.
            // TODO: 03.10.2019 Create unique playerId for the player to use
            String response = "206";
            OutputStream os = client.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(response);
            System.out.println("Message sent to the client is " + response);
            bw.flush();

            Connection connection = new Connection(client.getInetAddress(), Long.parseLong(response));
            Connections.getInstance().setConnection(connection);
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