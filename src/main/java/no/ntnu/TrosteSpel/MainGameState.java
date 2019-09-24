package no.ntnu.TrosteSpel;

import no.ntnu.TrosteSpel.entities.Movable;
import no.ntnu.TrosteSpel.entities.Projectile;
import no.ntnu.TrosteSpel.world.Map;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.util.ArrayList;

/**
 * A game using Slick2d
 */
public class MainGameState extends BasicGameState {

    /**
     * Screen width
     */
    private static final int WIDTH = 800;
    /**
     * Screen height
     */
    private static final int HEIGHT = 800;

    /**
     * A counter...
     */
    private int counter;
    private int id;

    private int deltaCount = 0;

    Vector2f mapLayout = new Vector2f();
    Map map;
    Vector2f[][] grid;
    ControlsConfig controls = ControlsConfig.getInstance();

    private float playerVel;
    private float lastX;
    private float lastY;


    private Movable player = new Movable(400, 300, new Circle(0, 0, 20));

    private ArrayList<Movable> movables = new ArrayList<Movable>();
    private ArrayList<Projectile> projectiles = new ArrayList<Projectile>();

    boolean[] keys = new boolean[512];

    private boolean debug = false;

    public MainGameState() {
        this.id = (int) System.currentTimeMillis();
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void init(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
        counter = 0;

        player.x = 350;
        player.y = 350;
        controls.setDefault();
        movables.add(player);
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        if (debug) {
            g.drawString("V: " + playerVel, 50, 50);
            g.drawString("X: " + player.x, 50, 70);
            g.drawString("Y: " + player.y, 50, 90);
        }
        float distanceX = Math.abs(player.x - lastX);
        float distanceY = Math.abs(player.y - lastY);

        playerVel = (float) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));

        lastX = player.x;
        lastY = player.y;
        g.drawOval(player.x,player.y,40,40);
}


    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        counter++;
        deltaCount += delta;
        movePlayer(delta);
    }

    private void movePlayer(int delta) {
        float lastPX = player.x;
        float lastPY = player.y;

        float deltaf = delta/1000f;

        // v = pixels per second
        float v = 1100f;
        float dv = deltaf * v;

        player.dx = 0;
        player.dy = 0;
        if (keys[controls.up()]) {
            player.dy = -dv;
        }
        if (keys[controls.down()]) {
            player.dy = dv;
        }
        if (keys[controls.left()]) {
            player.dx = -dv;
        }
        if (keys[controls.right()]) {
            player.dx = dv;
        }
        player.y += player.dy;
        player.x += player.dx;

    }


    @Override
    public void keyPressed(int key, char c) {
        keys[key] = true;
        if (keys[controls.debug()]) {
            debug ^= true;
        }
    }

    @Override
    public void keyReleased(int key, char c) {
        keys[key] = false;
    }
}
