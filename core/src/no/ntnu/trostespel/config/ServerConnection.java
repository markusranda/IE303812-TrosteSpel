package no.ntnu.trostespel.config;

import com.badlogic.gdx.Gdx;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class ServerConnection {
    private static Properties props;
    public static InetAddress host;
    public static int port;

    public ServerConnection() {

    }

    public static void loadDefault() {
        port = 7080;
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
    }
}
