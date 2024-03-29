package no.ntnu.trostespel.tcpServer;

import com.google.gson.Gson;
import no.ntnu.trostespel.model.Connection;
import no.ntnu.trostespel.model.ConnectionStatus;
import no.ntnu.trostespel.model.Connections;
import no.ntnu.trostespel.networking.tcp.TCPMessage;
import no.ntnu.trostespel.networking.tcp.Response;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

import static no.ntnu.trostespel.config.CommunicationConfig.MAX_PLAYERS;
import static no.ntnu.trostespel.model.ConnectionStatus.CONNECTED;
import static no.ntnu.trostespel.networking.tcp.TCPEvent.CONNECTION_ACCEPTED;
import static no.ntnu.trostespel.networking.tcp.TCPEvent.CONNECTION_REJECTED_SERVER_IS_FULL;


// TODO: 11.10.2019 This class should contain some sort of threadpool with a fixed size to handle all connection requests.

public class ConnectionManager implements Runnable {

    private final String mapFileName;
    private ServerSocket server;
    private Gson gson = new Gson();

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

            TCPMessage msg = deserialize(data);
            handlePackage(msg, client);


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

    private void handlePackage(TCPMessage msg, Socket client) throws IOException {
        switch (msg.getEvent()) {
            case CONNECT:
                String[] args = msg.getArgs();
                String uName = args[0];
                int udpPort = Integer.parseInt(args[1]);

                // Send the response back to the client.
                OutputStream os = client.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os);
                BufferedWriter bw = new BufferedWriter(osw);
                if (getActiveConnections() >= MAX_PLAYERS) {
                    String response = serialize(new Response(CONNECTION_REJECTED_SERVER_IS_FULL));
                    bw.write(response);
                    System.out.println("Message sent to the client is " + response);
                    bw.flush();
                } else {
                    Connection connection = new Connection(client.getInetAddress(), udpPort, uName);
                    String response = serialize(new Response(uName, connection.getPid(), mapFileName, CONNECTION_ACCEPTED));
                    bw.write(response);
                    System.out.println("Message sent to the client is " + response);
                    bw.flush();
                    Connections.getInstance().setConnection(connection);
                }
        }
    }

    private int getActiveConnections() {
        int activeClients = 0;
        for (Connection connection :
                Connections.getInstance().getConnections()) {
            if (connection.getConnectionStatus() == CONNECTED) activeClients++;
        }
        return activeClients;
    }

    private TCPMessage deserialize(String data) {
        return gson.fromJson(data, TCPMessage.class);
    }

    private String serialize(Object msg) {
        return gson.toJson(msg);
    }

    public void send() {

    }

    public SocketAddress getSocketAddress() {
        return this.server.getLocalSocketAddress();
    }
}