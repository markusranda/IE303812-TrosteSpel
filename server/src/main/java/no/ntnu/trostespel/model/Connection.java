package no.ntnu.trostespel.model;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicLong;

public class Connection {

    private InetAddress address;
    private int port;
    private DatagramSocket clientSocket;
    private double timeArrived;

    private String username;
    private long pid;
    private static AtomicLong idCounter = new AtomicLong(100);


    public Connection(InetAddress address, int port, String username) {
        this.address = address;
        this.pid = createID();
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


    public static long createID() {
        return idCounter.getAndIncrement();
    }
}
