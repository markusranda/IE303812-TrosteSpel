package no.ntnu.trostespel.tcpServer;

import com.google.gson.Gson;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.model.Connection;
import no.ntnu.trostespel.model.ConnectionStatus;
import no.ntnu.trostespel.model.Connections;
import no.ntnu.trostespel.networking.tcp.message.StringMessage;
import no.ntnu.trostespel.networking.tcp.message.TCPEvent;
import no.ntnu.trostespel.networking.tcp.message.TCPMessage;
import no.ntnu.trostespel.networking.tcp.message.ConnectionResponse;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static no.ntnu.trostespel.config.CommunicationConfig.MAX_PLAYERS;
import static no.ntnu.trostespel.model.ConnectionStatus.CONNECTED;
import static no.ntnu.trostespel.networking.tcp.message.TCPEvent.*;


// TODO: 11.10.2019 This class should contain some sort of threadpool with a fixed size to handle all connection requests.

public class ConnectionManager implements Runnable {

    private final String mapFileName;
    private ServerSocket server;
    private Gson gson;
    private volatile boolean running = false;

    public ConnectionManager(int port, String mapFileName) throws IOException {
        this.server = new ServerSocket(port, 1, null);
        this.mapFileName = mapFileName;
        gson = CommunicationConfig.getGsonForTcp();

    }

    @Override
    public void run() {
        running = true;
        while (running)
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

                //client.close();

            } catch (IOException io) {
                io.printStackTrace();
            }
    }

    List<PrintWriter> pws = new ArrayList<>();

    private void handlePackage(TCPMessage msg, Socket client) throws IOException {
        switch (msg.getEvent()) {
            case CONNECT:
                if (msg instanceof StringMessage) {
                    String[] args = ((StringMessage) msg).getArgs();
                    String uName = args[0];
                    int udpPort = Integer.parseInt(args[1]);

                    // Send the response back to the client.
                    OutputStream os = client.getOutputStream();
                    PrintWriter bw = new PrintWriter(os);
                    if (getActiveConnections() >= MAX_PLAYERS) {
                        String response = serialize(new ConnectionResponse(CONNECTION_REJECTED_SERVER_IS_FULL));
                        bw.println(response);
                        System.out.println("Message sent to the client is " + response);
                        bw.flush();
                    } else {
                        Connection connection = new Connection(client, udpPort, uName);
                        String response = serialize(new ConnectionResponse(uName, connection.getPid(), mapFileName, CONNECTION_ACCEPTED));
                        bw.println(response);
                        System.out.println("Message sent to the client is " + response);
                        bw.flush();
                        Connections.getInstance().setConnection(connection);
                        System.out.println();
                        System.out.println("Now serving " + Connections.getInstance().getConnections().size() + " player(s)");
                        System.out.println(Connections.getInstance().getConnections());
                    }
                }
                break;
            case GLOBAL_MESSAGE:
                OutputStream os = client.getOutputStream();
                PrintWriter bw = new PrintWriter(os);
                StringMessage stringMessage = new StringMessage(GLOBAL_MESSAGE);
                stringMessage.addMessage("123123123");
                bw.println(serialize(stringMessage));
                bw.flush();
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
        return gson.toJson(msg, TCPMessage.class);
    }

    public void send() {

    }

    public InetAddress getSocketAddress() {
        return this.server.getInetAddress();
    }
}