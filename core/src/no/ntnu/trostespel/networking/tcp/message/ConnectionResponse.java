package no.ntnu.trostespel.networking.tcp.message;

import java.net.DatagramSocket;

public class ConnectionResponse extends TCPMessage {
    private String mapFileName;
    private transient DatagramSocket socket;
    private String username;
    private long pid;


    public ConnectionResponse(String uName, long pid, String mapFileName, TCPEvent event) {
        super(event);
        this.username = uName;
        this.pid = pid;
        this.mapFileName = mapFileName;
    }

    public ConnectionResponse(TCPEvent event) {
        super(event);
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
