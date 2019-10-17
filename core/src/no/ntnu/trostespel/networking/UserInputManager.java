package no.ntnu.trostespel.networking;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import no.ntnu.trostespel.PlayerActions;
import no.ntnu.trostespel.config.KeyConfig;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.entity.Session;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Handles all inputs that should be processed by the server
 */
public class UserInputManager {

    private PlayerActions model;
    private int length;
    private DatagramPacket packet;
    private DatagramSocket socket;


    public UserInputManager(DatagramSocket socket) {
        model = new PlayerActions();
        Gson gson = new Gson();
        String jsonStr = gson.toJson(model);
        this.socket = socket;
        packet = new DatagramPacket(
                jsonStr.getBytes(),
                jsonStr.getBytes().length,
                CommunicationConfig.host,
                CommunicationConfig.SERVER_UDP_GAMEDATA_RECEIVE_PORT);
    }

    public void setPid(long pid) {
        model.pid = pid;
    }

    public void sendInput() {
        model.isup = Gdx.input.isKeyPressed(KeyConfig.up);
        model.isdown = Gdx.input.isKeyPressed(KeyConfig.down);
        model.isleft = Gdx.input.isKeyPressed(KeyConfig.left);
        model.isright = Gdx.input.isKeyPressed(KeyConfig.right);
        model.isattackUp = Gdx.input.isKeyPressed(KeyConfig.attackUp);
        model.isattackDown = Gdx.input.isKeyPressed(KeyConfig.attackDown);
        model.isattackLeft = Gdx.input.isKeyPressed(KeyConfig.attackLeft);
        model.isattackRight = Gdx.input.isKeyPressed(KeyConfig.attackRight);
        model.isaction1 = Gdx.input.isKeyPressed(KeyConfig.action1);
        model.isaction2 = Gdx.input.isKeyPressed(KeyConfig.action2);
        model.isaction3 = Gdx.input.isKeyPressed(KeyConfig.action3);
        String json = new Gson().toJson(model);
        packet.setData(json.getBytes());
        try {
            Session.getInstance().setPacketSendTime(System.currentTimeMillis());
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
