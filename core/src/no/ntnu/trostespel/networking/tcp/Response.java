package no.ntnu.trostespel.networking.tcp;

import java.net.DatagramSocket;

public class Response {
    private String mapFileName;
    private TCPEvent event;
    private transient DatagramSocket socket;
    private String username;
    private long pid;



    public Response(String uName, long pid, String mapFileName, TCPEvent event) {
        this.username = uName;
        this.pid = pid;
        this.mapFileName = mapFileName;
        this.event = event;
    }

    public Response(TCPEvent event) {
        this.event = event;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public String getUsername() {
        return username;
    }

    public long getPid() {
        return pid;
    }

    public String getMapFileName() {
        return mapFileName;
    }

    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public TCPEvent getEvent() {
        return this.event;
    }
}
