package no.ntnu.TrosteSpel;

import no.ntnu.TrosteSpel.gui.Button;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import javax.swing.plaf.nimbus.State;
import java.io.IOException;
import java.net.Socket;

public class MainMenu extends BasicGameState {

    private int id;

    private int width;
    private int height;

    private Button connectBtn;

    private int mainGameStateID;


    @Override
    public int getID() {
        return id;
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        this.id = (int) System.currentTimeMillis();
        this.connectBtn = new Button(container);
        connectBtn.addListener(source -> {
            //connectToOnline();
            game.enterState(Game.getMainGameStateID());
        });


    }

    private void connectToOnline() {
        try {
            Socket socket = new Socket("markus-sanntid.northeurope.cloudapp.azure.com", 7080);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        g.setColor(Color.darkGray);
        int x = container.getWidth();
        int y = container.getHeight();
        g.drawString("TROSTER I KAMP", x / 2, y / 3);
        connectBtn.render(container, g);
        connectBtn.setLocation(x / 2, y / 4);
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {

    }
}
