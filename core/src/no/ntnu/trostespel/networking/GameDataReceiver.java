package no.ntnu.trostespel.networking;

import com.badlogic.gdx.utils.Json;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import no.ntnu.trostespel.entity.Player;
import no.ntnu.trostespel.state.GameState;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.state.MovableState;
import no.ntnu.trostespel.state.PlayerState;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import static no.ntnu.trostespel.config.CommunicationConfig.CLIENT_UDP_GAMEDATA_RECEIVE_PORT;

/**
 * Listens for updates from server, and applies them to this GameState
 */
public class GameDataReceiver implements Runnable {

    private volatile GameState<PlayerState, MovableState> updatedGameState = new GameState<>();
    private Gson gson = new Gson();

    private final Type RECEIVED_DATA_TYPE = CommunicationConfig.RECEIVED_DATA_TYPE;

    @Override
    public void run() {
        DatagramSocket udpSocket = null;
        try {
            udpSocket = new DatagramSocket(CLIENT_UDP_GAMEDATA_RECEIVE_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        DatagramPacket packet;
        byte[] buf = new byte[256];

        while (true) {
            packet = new DatagramPacket(buf, buf.length);
            try {
                // blocks until a packet is received
                udpSocket.receive(packet);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String data = new String(packet.getData());
            StringReader sr = new StringReader(data);
            JsonReader reader = new JsonReader(sr);
            reader.setLenient(false);
            try {
                updatedGameState = gson.fromJson(reader, RECEIVED_DATA_TYPE);
            } catch (JsonIOException e) {
                e.printStackTrace();
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public GameState getUpdatedGameState() {
        return this.updatedGameState;
    }
}
