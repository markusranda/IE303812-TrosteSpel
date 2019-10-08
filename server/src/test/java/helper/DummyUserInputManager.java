package helper;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import no.ntnu.trostespel.PlayerActions;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * Handles all inputs that should be processed by the server
 */
public class DummyUserInputManager {

    private PlayerActions model;
    private int length;
    private DatagramPacket packet;
    private DatagramSocket socket;
    private final int port = 7080;
    private InetAddress host;
    private Random rand;

    public DummyUserInputManager() {
        rand = new Random();
        int pid = rand.nextInt();
        System.out.println(pid);
        model = new PlayerActions(pid);
    }


    public PlayerActions getRandomInput() {
        rand.nextBoolean();
        model.isup = rand.nextBoolean();
        model.isdown = rand.nextBoolean();
        model.isleft = rand.nextBoolean();
        model.isright = rand.nextBoolean();
        model.isattackUp = rand.nextBoolean();
        model.isattackDown = rand.nextBoolean();
        model.isattackLeft = rand.nextBoolean();
        model.isattackRight = rand.nextBoolean();
        model.isaction1 = rand.nextBoolean();
        model.isaction2 = rand.nextBoolean();
        model.isaction3 = rand.nextBoolean();
        return model;
    }
}
