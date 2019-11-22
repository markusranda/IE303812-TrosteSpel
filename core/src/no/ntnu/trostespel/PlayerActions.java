package no.ntnu.trostespel;

import java.util.Random;

/**
 * Model for building json object
 */
public class PlayerActions {

    public transient long time;
    public long pid = -1;
    public boolean isup = false;
    public boolean isright = false;
    public boolean isleft = false;
    public boolean isdown = false;
    public boolean isattackUp = false;
    public boolean isattackRight = false;
    public boolean isattackLeft = false;
    public boolean isattackDown = false;
    public boolean isaction1 = false;
    public boolean isaction2 = false;
    public boolean isaction3 = false;

    public PlayerActions(int pid) {
        this.pid = pid;
    }

    public PlayerActions() {
        pid = new Random().nextInt();
    }
}
