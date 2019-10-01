package no.ntnu.trostespel.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import no.ntnu.trostespel.config.KeyConfig;

import java.io.IOException;
import java.net.DatagramPacket;

public class NetworkedPlayerController extends ObjectController {

    private KeyConfig keys;
    private int pid;

    public NetworkedPlayerController(KeyConfig keys, int pid) {
        this.keys = keys;
        displacement = new Vector2();
        this.pid = pid;
    }

    @Override
    public Vector2 update(float delta) {
        float dv = 100 * delta;
        displacement.x = 0;
        displacement.y = 0;
        if (Gdx.input.isKeyPressed(keys.up)) {
            displacement.y += dv;
        }
        if (Gdx.input.isKeyPressed(keys.down)) {
            displacement.y += -dv;
        }
        if (Gdx.input.isKeyPressed(keys.left)) {
            displacement.x += -dv;
        }
        if (Gdx.input.isKeyPressed(keys.right)) {
            displacement.x += dv;
        }
        String s = "hello" + Gdx.input.isKeyPressed(keys.up);
        byte[] buffer = s.getBytes();
        DatagramPacket packet = new DatagramPacket(
                buffer, buffer.length, ServerConnection.address, ServerConnection.port);
        try {
            ServerConnection.socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return displacement;
    }
}
