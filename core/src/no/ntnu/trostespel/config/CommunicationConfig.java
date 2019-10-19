package no.ntnu.trostespel.config;

import com.google.gson.reflect.TypeToken;
import no.ntnu.trostespel.state.GameState;
import no.ntnu.trostespel.state.MovableState;
import no.ntnu.trostespel.state.PlayerState;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class CommunicationConfig {
    private static Properties props;
    public static InetAddress host;
    public static int SERVER_UDP_GAMEDATA_RECEIVE_PORT;
    public static int SERVER_TCP_CONNECTION_RECEIVE_PORT;
    public static int CLIENT_UDP_GAMEDATA_RECEIVE_PORT;
    public static int CLIENT_TCP_CONNECTION_RECEIVE_PORT;
    public static final int TICKRATE = 30;
    public static final int MAX_PLAYERS = 8;

    public static final Type RECEIVED_DATA_TYPE = new TypeToken<GameState<PlayerState, MovableState>>() {}.getType();

    private static CommunicationConfig single_instance = null;

    public static CommunicationConfig getInstance() {
        if (single_instance == null) {
            single_instance = new CommunicationConfig();
        }
        return single_instance;
    }

    private CommunicationConfig() {
        loadDefault();
    }


    private void loadDefault() {
        props = new Properties();
        try {
            props.load(new FileReader("server.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String savedHost = props.getProperty("host");
        if (savedHost.equals("localhost")) {
            try {
                host = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        } else {
            try {
                host = InetAddress.getByName(savedHost);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        try {
            SERVER_UDP_GAMEDATA_RECEIVE_PORT = Integer.parseInt((String) props.get("SERVER_UDP_GAMEDATA_RECEIVE_PORT"));
            SERVER_TCP_CONNECTION_RECEIVE_PORT = Integer.parseInt((String) props.get("SERVER_TCP_CONNECTION_RECEIVE_PORT"));
            CLIENT_UDP_GAMEDATA_RECEIVE_PORT = Integer.parseInt((String) props.get("CLIENT_UDP_GAMEDATA_RECEIVE_PORT"));
            CLIENT_TCP_CONNECTION_RECEIVE_PORT = Integer.parseInt((String) props.get("CLIENT_TCP_CONNECTION_RECEIVE_PORT"));
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
}
