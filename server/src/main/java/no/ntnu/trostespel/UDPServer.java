package no.ntnu.trostespel;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class UDPServer implements Runnable {
    private DatagramSocket udpSocket;
    private long counter = 0;
    private long t = 0;

    private Gson gson;


    public UDPServer(int port) throws IOException {
        this.udpSocket = new DatagramSocket(port);
        this.gson = new Gson();
    }


    @Override
    public void run() {
        try {
            System.out.println("-- Running Server at " + InetAddress.getLocalHost() + "--");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        PlayerUpdateDispatcher dispatcher = new PlayerUpdateDispatcher();

        while (true) {
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            // blocks until a packet is received
            try {
                udpSocket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // convert data to java object
            String data = new String(packet.getData());
            StringReader sr = new StringReader(data);
            JsonReader reader = new JsonReader(sr);
            reader.setLenient(true);
            PlayerActions actions = null;
            try {
                actions = gson.fromJson(reader, PlayerActions.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                System.out.println(data);
            } finally {
                sr.close();
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            dispatcher.queue(actions);
        }
    }
}
