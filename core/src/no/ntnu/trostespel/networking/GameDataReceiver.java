package no.ntnu.trostespel.networking;

import com.badlogic.gdx.utils.Json;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.MalformedJsonException;
import no.ntnu.trostespel.entity.Player;
import no.ntnu.trostespel.entity.Session;
import no.ntnu.trostespel.state.GameState;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.state.MovableState;
import no.ntnu.trostespel.state.PlayerState;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import static no.ntnu.trostespel.config.CommunicationConfig.BUF_LENGTH;
import static no.ntnu.trostespel.config.CommunicationConfig.CLIENT_UDP_GAMEDATA_RECEIVE_PORT;

/**
 * Listens for updates from server, and applies them to this GameState
 */
public class GameDataReceiver implements Runnable {

    private GameState<PlayerState, MovableState> updatedGameState = new GameState<>();

    private final Type RECEIVED_DATA_TYPE = CommunicationConfig.RECEIVED_DATA_TYPE;

    // Variables for parsing in-data
    private Gson gson = new Gson();
    private String data;
    private StringReader sr;
    private JsonReader reader;
    private DatagramSocket udpSocket;

    private long packetReceiveTime = 0;

    public GameDataReceiver(DatagramSocket socket) {
        this.udpSocket = socket;
    }

    @Override
    public void run() {

        DatagramPacket packet;
        byte[] buf = new byte[BUF_LENGTH];

        while (true) {
            packet = new DatagramPacket(buf, buf.length);
            try {
                // blocks until a packet is received
                udpSocket.receive(packet);
                calculateRtt(System.currentTimeMillis());
            } catch (Exception e) {
                e.printStackTrace();
            }

            data = new String(packet.getData());
            sr = new StringReader(data);
            reader = new JsonReader(sr);
            reader.setLenient(false);
            try {
                updatedGameState = gson.fromJson(reader, RECEIVED_DATA_TYPE);
                Session.getInstance().setReceivedGameState(updatedGameState);
            } catch (JsonIOException e) {
                e.printStackTrace();
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                System.out.println(data);
            } catch (Exception e) {
            }
        }
    }




    /**
     * Calculate round trip time of packet
     */
    private void calculateRtt(long packetReceiveTime) {
        long rtt = packetReceiveTime - Session.getInstance().getPacketSendTime();
    }
}
