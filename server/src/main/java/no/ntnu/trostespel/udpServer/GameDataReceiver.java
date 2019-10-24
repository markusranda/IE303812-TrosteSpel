package no.ntnu.trostespel.udpServer;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import no.ntnu.trostespel.PlayerActions;
import no.ntnu.trostespel.model.Connection;
import no.ntnu.trostespel.model.Connections;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class GameDataReceiver implements Runnable {
    private DatagramSocket udpSocket;
    private long counter = 0;
    private long t = 0;

    private Gson gson;

    private long startTime;
    private long nextPrint;

    private PlayerUpdateDispatcher dispatcher = new PlayerUpdateDispatcher();

    public GameDataReceiver(int port) throws IOException {
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
        startTime = System.currentTimeMillis();
        nextPrint = startTime + 10000;
        byte[] buf = new byte[2346];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        while (true) {
            try {
                // blocks until a packet is received
                udpSocket.receive(packet);

                // register arrival
                for (Connection con : Connections.getInstance().getConnections()) {
                    if (con.getAddress().equals(packet.getAddress())) {
                        con.setTimeArrivedToCurrentTime();
                        break;
                    }
                }

                // handle the packet
                handlePacket(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            countCmd();
        }
    }


    private String data;
    private StringReader sr;
    private JsonReader reader;
    PlayerActions actions = null;
    private void handlePacket(DatagramPacket packet) {
        // convert data to java object
        data = new String(packet.getData());
        sr = new StringReader(data);
        reader = new JsonReader(sr);
        reader.setLenient(true);
        try {
            actions = gson.fromJson(reader, PlayerActions.class);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(data);
            actions = null;
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (actions != null) {
            dispatcher.dispatch(actions);
        }
    }

    private long count = 0;
    private void countCmd() {
        count++;
        long time = System.currentTimeMillis();
        if (time > nextPrint) {
            System.out.println("Received " + count + " updates last 10 seconds");
            nextPrint += 10000;
            count = 0;
        }
    }
}
