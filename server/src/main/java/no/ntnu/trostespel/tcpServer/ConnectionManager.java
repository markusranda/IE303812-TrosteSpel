package no.ntnu.trostespel.tcpServer;

import com.google.gson.Gson;
import no.ntnu.trostespel.model.Connection;
import no.ntnu.trostespel.model.Connections;
import no.ntnu.trostespel.networking.tcp.TCPMessage;
import no.ntnu.trostespel.networking.tcp.Response;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


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
                Connection connection = new Connection(client.getInetAddress(), udpPort, uName);
                OutputStream os = client.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os);
                BufferedWriter bw = new BufferedWriter(osw);
                String response = serialize(new Response(uName, connection.getPid(), mapFileName));
                bw.write(response);
                System.out.println("Message sent to the client is " + response);
                bw.flush();
                Connections.getInstance().setConnection(connection);
        }
    }

    private TCPMessage deserialize(String data) {
        return gson.fromJson(data, TCPMessage.class);
    }

    private String serialize(Response msg) {
        return gson.toJson(msg);
    }

    public void send() {

    }

    public InetAddress getSocketAddress() {
        return this.server.getInetAddress();
    }
}