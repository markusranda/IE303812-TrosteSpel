package no.ntnu.trostespel.networking.tcp;

public class TCPMessage {

    private TCPEvent event;
    private String[] args;

    public TCPMessage(TCPEvent event, String[] args) {
        this.event = event;
        this.args = args;
    }

    public TCPEvent getEvent() {
        return event;
    }

    public String[] getArgs() {
        return args;
    }

}
