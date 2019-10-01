package no.ntnu.trostespel.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import no.ntnu.trostespel.config.KeyConfig;
import no.ntnu.trostespel.config.ServerConnection;
import no.ntnu.trostespel.networking.ServerConnector;
import no.ntnu.trostespel.networking.UserInputManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class NetworkedPlayerController extends ObjectController {

    private int pid;
    private UserInputManager manager;


    public NetworkedPlayerController(KeyConfig keys, int pid) {
        displacement = new Vector2();
    }

    @Override
    public Vector2 update(float delta) {

        //TODO: Receive position info from server

        return displacement;
    }
}
