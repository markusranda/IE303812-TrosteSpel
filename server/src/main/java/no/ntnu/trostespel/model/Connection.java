package no.ntnu.trostespel.model;

import no.ntnu.trostespel.CircularArrayList;

import java.net.InetAddress;

public class Connection {

    private static final int SNAPSHOP_ARRAY_MAX_SIZE = 32;
    private InetAddress address;
    private long playerId;
    private CircularArrayList snapshotArray;

    public Connection(InetAddress address, long playerId) {
        this.address = address;
        this.playerId = playerId;
        this.snapshotArray = new CircularArrayList(SNAPSHOP_ARRAY_MAX_SIZE);
    }

    public CircularArrayList getSnapshotArray() {
        return snapshotArray;
    }

    public InetAddress getAddress() {
        return address;
    }

    public long getPlayerId() {
        return playerId;
    }
}
