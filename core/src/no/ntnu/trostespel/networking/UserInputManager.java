package no.ntnu.trostespel.networking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.google.gson.Gson;
import no.ntnu.trostespel.PlayerActions;
import no.ntnu.trostespel.config.KeyConfig;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.entity.Movable;
import no.ntnu.trostespel.entity.Player;
import no.ntnu.trostespel.entity.Session;
import no.ntnu.trostespel.state.GameState;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Handles all inputs that should be processed by the server
 */
public class UserInputManager {

    private final GameState<Player, Movable> gameState;
    private PlayerActions model;
    private int length;
    private DatagramPacket packet;
    private DatagramSocket socket;
    private Player myPlayer;


    public UserInputManager(DatagramSocket socket, GameState<Player, Movable> gameState) {
        model = new PlayerActions();
        Gson gson = new Gson();
        String jsonStr = gson.toJson(model);
        this.socket = socket;
        packet = new DatagramPacket(
                jsonStr.getBytes(),
                jsonStr.getBytes().length,
                CommunicationConfig.host,
                CommunicationConfig.SERVER_UDP_GAMEDATA_RECEIVE_PORT);
        this.gameState = gameState;
    }

    public void setPid(long pid) {
        model.pid = pid;
    }

    public void sendInput() {
        if (!gameState.players.isEmpty()) {
            myPlayer = gameState.players.get(Session.getInstance().getPid());
        }
        if (canUp(myPlayer)) {
            model.isup = Gdx.input.isKeyPressed(KeyConfig.up);
        } else {
            model.isup = false;
        }
        if (canDown(myPlayer)) {
            model.isdown = Gdx.input.isKeyPressed(KeyConfig.down);
        } else {
            model.isdown = false;
        }
        if (canLeft(myPlayer)) {
            model.isleft = Gdx.input.isKeyPressed(KeyConfig.left);
        } else {
            model.isleft = false;
        }
        if (canRight(myPlayer)) {
            model.isright = Gdx.input.isKeyPressed(KeyConfig.right);
        } else {
            model.isright = false;
        }
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

    private boolean canRight(Player myPlayer) {
        if (myPlayer != null) {
            if ((int) myPlayer.getPos().x + 2 > 1023 - 64) {
                return false;
            }
        }
        return true;
    }

    private boolean canLeft(Player myPlayer) {
        if (myPlayer != null) {
            if ((int) myPlayer.getPos().x - 2 < 0) {
                return false;
            }
        }
        return true;
    }

    private boolean canDown(Player myPlayer) {
        if (myPlayer != null) {
            if ((int) myPlayer.getPos().y - 2 < 0) {
                return false;
            }
        }
        return true;
    }

    private boolean canUp(Player myPlayer) {
        if (myPlayer != null) {
            if ((int) myPlayer.getPos().y + 2 > 1023 - 64) {
                return false;
            }
        }
        return true;
    }
}