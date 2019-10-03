package no.ntnu.trostespel;

import com.badlogic.gdx.utils.Json;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

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
            Gson gson = new GsonBuilder().setLenient().create();
            String data = new String(packet.getData());
            Json json = new Json();
            UserInputManagerModel actions = gson.fromJson(data,UserInputManagerModel.class);
            dispatcher.queue(actions);
        }
    }
}
