package no.ntnu.trostespel.networking.tcp;

import java.net.DatagramSocket;

public class Response {
    private final String mapFileName;
    private transient DatagramSocket socket;
    private String username;
    private long pid;

    public Response(DatagramSocket socket, String username, long pid, String mapFileName) {
        this.socket = socket;
        this.username = username;
        this.pid = pid;
        this.mapFileName = mapFileName;
    }


    public Response(String username, long pid, String mapFileName) {
        this.socket = socket;
        this.username = username;
        this.pid = pid;
        this.mapFileName = mapFileName;
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


}
