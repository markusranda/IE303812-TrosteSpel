package no.ntnu.trostespel.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import no.ntnu.trostespel.config.KeyConfig;
import no.ntnu.trostespel.config.PlayerKeyConfig;
import no.ntnu.trostespel.config.ServerConnection;
import no.ntnu.trostespel.networking.ServerConnector;
import no.ntnu.trostespel.networking.UserInputManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class NetworkedPlayerController extends ObjectController {

    public NetworkedPlayerController() {
        displacement = new Vector2();
    }

    @Override
    public Vector2 update(float delta) {
        displacement.setZero();
        //TODO: Receive position info from server
        float dv = 300 * delta;
        if (Gdx.input.isKeyPressed(PlayerKeyConfig.up)) {
            displacement.y += dv;
        }
        if (Gdx.input.isKeyPressed(PlayerKeyConfig.down)) {
            displacement.y += -dv;
        }
        if (Gdx.input.isKeyPressed(PlayerKeyConfig.left)) {
            displacement.x += -dv;
        }
        if (Gdx.input.isKeyPressed(PlayerKeyConfig.right)) {
            displacement.x += dv;
        }
        displacement.limit(dv);
        return displacement;
    }
}
