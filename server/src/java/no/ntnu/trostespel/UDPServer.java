package no.ntnu.trostespel;

import java.io.IOException;
import java.net.*;

public class UDPServer implements Runnable {
    private DatagramSocket udpSocket;


    public UDPServer(int port) throws IOException {
        this.udpSocket = new DatagramSocket(port);
    }


    @Override
    public void run() {
        try {
            System.out.println("-- Running Server at " + InetAddress.getLocalHost() + "--");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String msg;

        while (true) {

            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            // blocks until a packet is received
            try {
                udpSocket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            msg = new String(packet.getData()).trim();

            System.out.println(
                    "UDP Message from " + packet.getAddress().getHostAddress() + ": " + msg);

        }
    }
}
