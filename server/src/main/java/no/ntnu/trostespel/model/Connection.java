package no.ntnu.trostespel.model;

import java.net.InetAddress;

public class Connection {

    private InetAddress address;
    private long playerId;

    public Connection(InetAddress address, long playerId) {
        this.address = address;
        this.playerId = playerId;
    }

    public InetAddress getAddress() {
        return address;
    }

    public long getPlayerId() {
        return playerId;
    }
}
