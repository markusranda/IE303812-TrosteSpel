package no.ntnu.trostespel.udpServer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import no.ntnu.trostespel.PlayerActions;

import java.net.DatagramPacket;

public class PacketDeserializerKryo {

    private Kryo kryo;
    private Input input;
    private PlayerActions actions;

    public PacketDeserializerKryo() {
        kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        input = new Input();
    }

    public PlayerActions deserialize(DatagramPacket packet) {
        input.setBuffer(packet.getData());
        actions = null;
        actions = kryo.readObject(input, PlayerActions.class);
        return actions;
    }
}
