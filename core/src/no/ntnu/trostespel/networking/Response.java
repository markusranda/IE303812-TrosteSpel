package no.ntnu.trostespel.networking;

import java.io.StringReader;
import java.net.DatagramSocket;

public class Response {
    private DatagramSocket socket;
    private String username;
    private long pid;

    public Response(DatagramSocket socket, String username, long pid) {
        this.socket = socket;
        this.username = username;
        this.pid = pid;
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
}
