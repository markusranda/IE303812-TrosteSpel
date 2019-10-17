package no.ntnu.trostespel;

import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.udp.GameDataReceiver;

public class ServerLauncher {

    public static void main(String[] args) throws Exception {
        // Initialize ConnectionConfig
        CommunicationConfig.getInstance();

        GameDataReceiver UDPClient = new GameDataReceiver(CommunicationConfig.SERVER_UDP_GAMEDATA_RECEIVE_PORT);
        ConnectionManager TCPClient = new ConnectionManager(CommunicationConfig.SERVER_TCP_CONNECTION_RECEIVE_PORT);

        Thread UdpThread = new Thread(UDPClient);
        Thread TcpThread = new Thread(TCPClient);
        UdpThread.setName("UDPClient");
        TcpThread.setName("TCPClient");

        UdpThread.start();
        TcpThread.start();

        new GameServer();
    }
}