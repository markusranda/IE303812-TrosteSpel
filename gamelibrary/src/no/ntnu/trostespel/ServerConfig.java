package no.ntnu.trostespel;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class ServerConfig {

    public static final int MAX_PLAYERS = 8;
    public static final int TICKRATE = 32;
    public static final int UDP_PORT = 7080;
    public static final int TCP_PORT = 7083;

    private static InetAddress host;
    private static Properties props;
}

