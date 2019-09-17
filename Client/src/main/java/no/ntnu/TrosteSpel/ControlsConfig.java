package no.ntnu.TrosteSpel;

import org.newdawn.slick.Input;

public class ControlsConfig {

    private static ControlsConfig instance = null;

    public static ControlsConfig getInstance()
    {
        if (instance == null)
            instance = new ControlsConfig();

        return instance;
    }

    private int up;
    private int down;
    private int left;
    private int right;

    public void setDefault() {
        up = Input.KEY_W;
        down = Input.KEY_S;
        left = Input.KEY_A;
        right = Input.KEY_D;
    }



    public void setUp(int up) {
        this.up = up;
    }

    public void setDown(int down) {
        this.down = down;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int up() {
        return up;
    }

    public int down() {
        return down;
    }

    public int left() {
        return left;
    }

    public int right() {
        return right;
    }
}
