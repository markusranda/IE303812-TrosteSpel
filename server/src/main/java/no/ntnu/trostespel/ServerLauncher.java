package no.ntnu.trostespel;

import no.ntnu.trostespel.config.CommunicationConfig;

public class ServerLauncher {

    public static void main(String[] args) throws Exception {
        // Initialize ConnectionConfig
        CommunicationConfig.getInstance();

        // Start GameServer
        new GameServer();
    }
}