package no.ntnu.trostespel;

import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.udpServer.GameDataReceiver;
import no.ntnu.trostespel.udpServer.GameServer;

public class ServerLauncher {

    public static void main(String[] args) throws Exception {
        // Initialize ConnectionConfig
        CommunicationConfig.getInstance();

        // Start GameServer
        new GameServer();
    }
}