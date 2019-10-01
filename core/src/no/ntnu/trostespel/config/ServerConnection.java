package no.ntnu.trostespel.config;

import java.net.*;

public class ServerConnection {
    static byte[] buffer = new byte[256];
    public static InetAddress address;
    public static int port = 7080;
    public static DatagramSocket socket;


    public static void load() {
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        DatagramPacket packet = new DatagramPacket(
                buffer, buffer.length, address, 9000);
    }
}
