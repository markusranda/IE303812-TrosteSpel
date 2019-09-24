package no.ntnu.TrosteSpel;

import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

/*
 * TODO:
 *
 * - At least one more weapon
 * - More enemy types?
 * - Levels!
 * - Health powerups (dropped randomly from enemies?)
 * - Destructible barrels!
 *
 */

public class Game extends StateBasedGame {

    private static final String name = "Troster i kamp";
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    public static final String SERVER_ADDRESS = "markus-sanntid.northeurope.cloudapp.azure.com";
    public static final int SERVER_UDP_PORT = 7080;

    private static AppGameContainer container;

    private static MainGameState mgs;
    private static MainMenu menu;

    /**
     * Create a new state based game
     *
     * @param name The name of the game
     */
    public Game(String name) {
        super(name);
    }

    public static void main(String[] args) {
        try {
            container = new AppGameContainer(new Game("Troster i kamp"));

            //container.setTargetFrameRate(60);
            //container.setMinimumLogicUpdateInterval(15);
            //container.setMaximumLogicUpdateInterval(15);

            container.setDisplayMode(WIDTH,HEIGHT,false);
            container.setShowFPS(true);
            container.start();

        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public void initStatesList(GameContainer container) {
        mgs = new MainGameState();
        menu = new MainMenu();
        addState(mgs);
        addState(menu);
        enterState(menu.getID());
    }

    public static int getMainGameStateID() {
        return mgs.getID();
    }

    public void mousePressed(int button, int x, int y) {
        getCurrentState().mousePressed(button,x,y);
    }
    public void mouseReleased(int button, int x, int y) {
        getCurrentState().mouseReleased(button,x,y);
    }
    public void mouseMoved(int oldx, int oldy, int newx, int newy) {
        getCurrentState().mouseMoved(oldx,oldy,newx,newy);
    }
    public void keyPressed(int key, char c) {
        getCurrentState().keyPressed(key,c);
    }
    public void keyReleased(int key, char c) {
        getCurrentState().keyReleased(key,c);
    }

}