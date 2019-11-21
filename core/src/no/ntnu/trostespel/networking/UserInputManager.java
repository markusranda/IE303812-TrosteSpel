package no.ntnu.trostespel.networking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.google.gson.Gson;
import no.ntnu.trostespel.Direction;
import no.ntnu.trostespel.PlayerActions;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.config.GameRules;
import no.ntnu.trostespel.config.KeyConfig;
import no.ntnu.trostespel.entity.Movable;
import no.ntnu.trostespel.entity.Player;
import no.ntnu.trostespel.entity.Session;
import no.ntnu.trostespel.state.GameState;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

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

    private boolean canLeft = false;
    private boolean canRight = false;
    private boolean canUp = false;
    private boolean canDown = false;

    private static ArrayList<Rectangle> collideables;

    private Vector2 playerCenter = new Vector2();
    private Vector2 recCenter = new Vector2();
    private Vector2 unitVector = new Vector2();
    private Vector2 resultVector = new Vector2();


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
        getStaticCollideables(gameState.getCollidables());
    }

    public void setPid(long pid) {
        model.pid = pid;
    }

    private void getStaticCollideables(TiledMapTileLayer layer) {
        collideables = new ArrayList<>();
        int h = layer.getHeight();
        int w = layer.getWidth();
        float tileW = layer.getTileWidth();
        float tileH = layer.getTileHeight();
        for (int i = 1; i < w; i++) {
            for (int j = 1; j < h; j++) {
                TiledMapTileLayer.Cell cell = layer.getCell(i, j);
                if (cell != null) {
                    if (cell.getTile() != null) {
                        TextureRegion region = cell.getTile().getTextureRegion();
                        float x = i * tileW;
                        float y = j * tileH;

                        Rectangle rec = new Rectangle(x, y, region.getRegionWidth(), region.getRegionHeight());
                        collideables.add(rec);
                    }
                }
            }

        }
    }

    public void sendInput() {

        if (!gameState.getPlayers().isEmpty()) {
            myPlayer = gameState.getPlayers().get(Session.getInstance().getPid());
        }

        if (canUp()) {
            model.isup = Gdx.input.isKeyPressed(KeyConfig.up);
            if (model.isup) {
                model.isup = willCollide(Direction.up);
            }
        } else {
            model.isup = false;
        }
        if (canDown()) {
            model.isdown = Gdx.input.isKeyPressed(KeyConfig.down);
            if (model.isdown) {
                model.isdown = willCollide(Direction.down);
            }
        } else {
            model.isdown = false;
        }
        if (canLeft()) {
            model.isleft = Gdx.input.isKeyPressed(KeyConfig.left);
            if (model.isleft) {
                model.isleft = willCollide(Direction.left);
            }
        } else {
            model.isleft = false;
        }
        if (canRight()) {
            model.isright = Gdx.input.isKeyPressed(KeyConfig.right);
            if (model.isright) {
                model.isright = willCollide(Direction.right);
            }
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

        // Set sequence number for this cmd
        long curSeqNum = model.seqNum++;
        // Save it to a queue
        Session.getInstance().addSeqNum(curSeqNum);

        String json = new Gson().toJson(model);
        packet.setData(json.getBytes());

        if (gameState.getPlayers().containsKey(Session.getInstance().getPid())) {
            interpolateMovement(myPlayer, model);
        }

        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void interpolateMovement(Player myPlayer, PlayerActions action) {
        Vector2 playerPos = myPlayer.getPos();

        if (action.isleft) {
            playerPos.x += -GameRules.Player.SPEED;
        }
        if (action.isright) {
            playerPos.x += GameRules.Player.SPEED;
        }
        if (action.isup) {
            playerPos.y += GameRules.Player.SPEED;
        }
        if (action.isdown) {
            playerPos.y += -GameRules.Player.SPEED;
        }

        myPlayer.setPos(playerPos);
    }

    private boolean canRight() {
        if (myPlayer != null) {
            if ((int) myPlayer.getPos().x + 2 > gameState.getCollidables().getWidth() * gameState.getCollidables().getTileWidth() - 64) {
                return false;
            }
        }
        return true;
    }

    private boolean canLeft() {
        if (myPlayer != null) {
            if ((int) myPlayer.getPos().x - 2 < 0) {
                return false;
            }
        }
        return true;
    }

    private boolean canDown() {
        if (myPlayer != null) {
            if ((int) myPlayer.getPos().y - 2 < 0) {
                return false;
            }
        }
        return true;
    }

    private boolean canUp() {
        if (myPlayer != null) {
            if ((int) myPlayer.getPos().y + 2 > gameState.getCollidables().getHeight() * gameState.getCollidables().getTileHeight() - 64) {
                return false;
            }
        }
        return true;
    }


    private boolean willCollide(Direction dir) {
        Rectangle player = new Rectangle(myPlayer.getHitbox());

        double vel = GameRules.Player.SPEED;
        switch (dir) {
            case up:
                player.y += vel;
                unitVector.set(0, 1);
                break;
            case down:
                player.y -= vel;
                unitVector.set(0, -1);
                break;
            case right:
                player.x += vel;
                unitVector.set(1, 0);
                break;
            case left:
                player.x -= vel;
                unitVector.set(-1, 0);
                break;
        }
        for (Rectangle rec : collideables) {
            if (Intersector.overlaps(player, rec)) {
                rec.getCenter(recCenter);
                player.getCenter(playerCenter);
                recCenter.sub(playerCenter).nor();
                float dot = unitVector.dot(recCenter);

                if (dot <= 0) {
                    return true;
                }
                return false;
            }
        }
        return true;
    }

    public static ArrayList<Rectangle> getCollideables() {
        return collideables;
    }
}