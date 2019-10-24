package no.ntnu.trostespel.model;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class Connection {

    private InetAddress address;
    private int port;
    private DatagramSocket clientSocket;
    private double timeArrived;

    private String username;
    private long pid;


    public Connection(InetAddress address, int port, long pid, String username) {
        this.address = address;
        this.pid = pid;
        this.username = username;
        this.port = port;
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

    public int getPort() {
        return port;
    }
}
