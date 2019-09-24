package no.ntnu.TrosteSpel.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.GUIContext;

public class Button extends AbstractComponent {
    /**
     * Create a new component
     *
     * @param container The container displaying this component
     */

    private int x;
    private int y;
    private int w;
    private int h;

    private int dx;
    private int dy;

    private String test ="";

    private boolean hovered = false;

    public Button(GUIContext container) {
        super(container);
        this.x = 0;
        this.y = 0;
    }

    @Override
    public void render(GUIContext container, Graphics g) throws SlickException {
        this.w = 200;
        this.h = 70;
        g.setColor(Color.green);
        if (hovered) {
            g.setColor(Color.yellow);
        }
        g.drawRect(dx, dy, 200, 70);
        g.drawString("Connect", dx, dy);
        g.drawString(test, dx, dy + 100);
    }


    @Override
    public void addListener(ComponentListener listener) {
        super.addListener(listener);
    }

    @Override
    protected void notifyListeners() {
        super.notifyListeners();
    }

    @Override
    protected void consumeEvent() {
        super.consumeEvent();
    }

    @Override
    public void mouseMoved(int oldx, int oldy, int newx, int newy) {
        super.mouseMoved(oldx, oldy, newx, newy);
        if ((newx > dx && newx < dx + w) && (newy > dy && newy < dy + h)) {
            hovered = true;

        } else {
            hovered = false;
        }
    }

    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) {
        super.mouseClicked(button, x, y, clickCount);
        if (hovered) {
            notifyListeners();
        }
    }

    @Override
    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
        this.dx = x - w/2;
        this.dy = y - h/2;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }
}
