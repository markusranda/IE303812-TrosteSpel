package no.ntnu.trostespel.networking.tcp.message;

public abstract class TCPMessage {

    private TCPEvent event;

    public TCPMessage(TCPEvent event) {
        this.event = event;
    }

    public TCPEvent getEvent() {
        return event;
    }

    public void setEvent(TCPEvent event) {
        this.event = event;
    }
}
