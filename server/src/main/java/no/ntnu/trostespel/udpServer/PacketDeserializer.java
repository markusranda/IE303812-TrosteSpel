package no.ntnu.trostespel.udpServer;

import com.badlogic.gdx.utils.Pool;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import no.ntnu.trostespel.PlayerActions;

import java.io.IOException;
import java.io.StringReader;
import java.net.DatagramPacket;

public class PacketDeserializer{

    private Gson gson = new Gson();
    private String data;
    private StringReader sr;
    private JsonReader reader;
    PlayerActions actions;

    public PlayerActions deserialize(DatagramPacket packet) {
        // each player only gets one update per tick
        // excess updates get discarded
        data = new String(packet.getData());
        sr = new StringReader(data);
        reader = new JsonReader(sr);
        actions = null;
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
        return actions;
    }

}
