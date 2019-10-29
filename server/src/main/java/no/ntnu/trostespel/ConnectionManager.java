package no.ntnu.trostespel;

import no.ntnu.trostespel.model.Connection;
import no.ntnu.trostespel.model.Connections;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


// TODO: 11.10.2019 This class should contain some sort of threadpool with a fixed size to handle all connection requests.

public class ConnectionManager implements Runnable {

    private final String mapFileName;
    private ServerSocket server;
    long pid = 100;
    private boolean firstTimeRunning = true;

    public ConnectionManager(int port, String mapFileName) throws IOException {
        this.server = new ServerSocket(port, 1, null);
        this.mapFileName = mapFileName;
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
            String[] msg = data.split(" ");
            String uName = msg[0];
            String udpPortStr = msg[1];
            int udpPort = Integer.parseInt(udpPortStr);

            // Send the response back to the client.
            Connection connection = new Connection(client.getInetAddress(), udpPort, uName);
            OutputStream os = client.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);

            String response = connection.getPid() + " " + mapFileName;
            bw.write(response);
            System.out.println("Message sent to the client is " + response);
            bw.flush();

            Connections.getInstance().setConnection(connection);
            client.close();
            System.out.println();
            System.out.println("Now serving " + Connections.getInstance().getConnections().size() + " player(s)");
            System.out.println(Connections.getInstance().getConnections());
        } catch (IOException io) {
            io.printStackTrace();
            run();
        }
        run();
    }

    public InetAddress getSocketAddress() {
        return this.server.getInetAddress();
    }
}