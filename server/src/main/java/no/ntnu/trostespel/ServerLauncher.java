package no.ntnu.trostespel;

import no.ntnu.trostespel.config.ConnectionConfig;

public class ServerLauncher {

    public static void main(String[] args) throws Exception {
        // Initialize ConnectionConfig
        ConnectionConfig.getInstance();

        GameDataReceiver UDPClient = new GameDataReceiver(ConnectionConfig.SERVER_UDP_GAMEDATA_RECEIVE_PORT);
        ConnectionManager TCPClient = new ConnectionManager(ConnectionConfig.SERVER_TCP_CONNECTION_RECEIVE_PORT);

        Thread t1 = new Thread(UDPClient);
        Thread t2 = new Thread(TCPClient);
        t1.setName("UDPClient");
        t2.setName("TCPClient");
//        t1.start();
        t2.start();

        new GameServer();
    }
}