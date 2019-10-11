package no.ntnu.trostespel.config;

import com.badlogic.gdx.Gdx;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class ConnectionConfig {
    private static Properties props;
    public static InetAddress host;
    public static int SERVER_UDP_GAMEDATA_RECEIVE_PORT;
    public static int SERVER_TCP_CONNECTION_RECEIVE_PORT;
    public static int CLIENT_UDP_GAMEDATA_RECEIVE_PORT;
    public static int CLIENT_TCP_CONNECTION_RECEIVE_PORT;

    private static ConnectionConfig single_instance = null;

    public static ConnectionConfig getInstance() {
        if (single_instance == null) {
            single_instance = new ConnectionConfig();
        }
        return single_instance;
    }

    private ConnectionConfig() {
        loadDefault();
    }


    private void loadDefault() {
        props = new Properties();
        try {
            props.load(new FileReader(Gdx.files.getLocalStoragePath() + File.separator + "server.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String savedHost= props.getProperty("host");
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
            SERVER_UDP_GAMEDATA_RECEIVE_PORT = (int) props.get("SERVER_UDP_GAMEDATA_RECEIVE_PORT");
            SERVER_TCP_CONNECTION_RECEIVE_PORT = (int) props.get("SERVER_TCP_CONNECTION_RECEIVE_PORT");
            CLIENT_UDP_GAMEDATA_RECEIVE_PORT = (int) props.get("CLIENT_UDP_GAMEDATA_RECEIVE_PORT");
            CLIENT_TCP_CONNECTION_RECEIVE_PORT = (int) props.get("CLIENT_TCP_CONNECTION_RECEIVE_PORT");
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
}
