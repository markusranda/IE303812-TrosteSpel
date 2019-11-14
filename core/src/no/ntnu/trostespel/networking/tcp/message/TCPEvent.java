package no.ntnu.trostespel.networking.tcp.message;

public enum TCPEvent {
    CONNECT,
    DISCONNECT,
    CONNECTION_REJECTED_SERVER_IS_FULL,
    CONNECTION_ACCEPTED,
    GLOBAL_MESSAGE
}
