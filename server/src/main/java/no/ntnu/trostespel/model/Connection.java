package no.ntnu.trostespel.model;

import java.net.InetAddress;

public class Connection {

    private InetAddress address;
    private long playerId;
    private double timeArrived;
    private boolean remove = false;

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

    public double getTimeArrived() {
        return timeArrived;
    }

    public void setTimeArrivedToCurrentTime() {
        this.timeArrived = System.currentTimeMillis();
    }

    public void setToRemove() {
        this.remove = true;
    }

    public boolean isRemove() {
        return remove;
    }
}
