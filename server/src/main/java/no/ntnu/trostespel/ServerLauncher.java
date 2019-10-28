package no.ntnu.trostespel;

import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.udpServer.GameDataReceiver;
import no.ntnu.trostespel.udpServer.GameServer;

public class ServerLauncher {

    public static void main(String[] args) throws Exception {
        // Initialize ConnectionConfig
        CommunicationConfig.getInstance();

        GameServer UDPServer = new GameServer();
        ConnectionManager TCPClient = new ConnectionManager(CommunicationConfig.SERVER_TCP_CONNECTION_RECEIVE_PORT);

        Thread UdpThread = new Thread(UDPServer);
        Thread TcpThread = new Thread(TCPClient);
        UdpThread.setName("Server-Main");
        TcpThread.setName("ConnectionClient");

        UdpThread.start();
        TcpThread.start();
    }
}