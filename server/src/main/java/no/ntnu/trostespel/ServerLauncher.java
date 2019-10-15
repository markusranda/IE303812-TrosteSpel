package no.ntnu.trostespel;

import no.ntnu.trostespel.config.ConnectionConfig;

public class ServerLauncher {

    public static void main(String[] args) throws Exception {
        // Initialize ConnectionConfig
        ConnectionConfig.getInstance();

        GameDataReceiver UDPClient = new GameDataReceiver(ConnectionConfig.SERVER_UDP_GAMEDATA_RECEIVE_PORT);
        ConnectionManager TCPClient = new ConnectionManager(ConnectionConfig.SERVER_TCP_CONNECTION_RECEIVE_PORT);

        Thread UdpThread = new Thread(UDPClient);
        Thread TcpThread = new Thread(TCPClient);
        UdpThread.setName("UDPClient");
        TcpThread.setName("TCPClient");

        UdpThread.start();
        TcpThread.start();

        new GameServer();
    }
}