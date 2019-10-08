package no.ntnu.trostespel;

import java.util.ArrayList;
import java.util.List;

public class Connections {

    private List<Connection> connections;

    // static variable single_instance of type Singleton
    private static Connections single_instance = null;

    public static Connections getInstance() {
        if (single_instance == null) {
            single_instance = new Connections();
        }
        return single_instance;
    }

    private Connections() {
        connections = new ArrayList<>();
    }

    List<Connection> getConnections() {
        return connections;
    }

    void setConnection(Connection connection) {
        getConnections().add(connection);
        System.out.println("New connection added: " + connection.getAddress() + " - " + connection.getPlayerId());
    }

    void removeConnection(Connection connection) {
        getConnections().remove(connection);
        System.out.println("Removed connection: " + connection.getAddress());
    }
}
