package no.ntnu.trostespel.networking;

import com.google.gson.Gson;
import no.ntnu.trostespel.Direction;
import no.ntnu.trostespel.GameState;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import static no.ntnu.trostespel.config.ConnectionConfig.CLIENT_UDP_GAMEDATA_RECEIVE_PORT;

/**
 * Listens for updates from server, and applies them to this GameState
 */
public class GameDataReceiver implements Runnable {

    @Override
    public void run() {
        DatagramSocket udpSocket = null;
        try {
            udpSocket = new DatagramSocket(CLIENT_UDP_GAMEDATA_RECEIVE_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        while (true) {
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                // blocks until a packet is received
                udpSocket.receive(packet);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String json = new String(packet.getData());
            Gson gson = new Gson();
//            GameState gameState = gson.fromJson(json, GameState.class);
//          TODO: 11.10.2019 Apply this new GameState, instead of just printing it
//            System.out.print(gameState.getEntities());
            System.out.println(json);
        }
    }
}
