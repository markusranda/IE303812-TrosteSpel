package no.ntnu.trostespel.model;

import java.net.InetAddress;

public class Connection {

    private InetAddress address;
    private String username;
    private long pid;
    private double timeArrived;

    public Connection(InetAddress address, long pid, String username) {
        this.address = address;
        this.pid = pid;
        this.username = username;
    }

    public InetAddress getAddress() {
        return address;
    }

    public long getPid() {
        return pid;
    }

    public double getTimeArrived() {
        return timeArrived;
    }

    public void setTimeArrivedToCurrentTime() {
        this.timeArrived = System.currentTimeMillis();
    }

    public String getUsername() {
        return username;
    }
}
