package no.ntnu.trostespel.config;

import com.badlogic.gdx.Input.Keys;

public class PlayerKeyConfig {
    public static int up;
    public static int right;
    public static int left;
    public static int down;

    public static int attackUp;
    public static int attackRight;
    public static int attackLeft;
    public static int attackDown;

    public static int action1;
    public static int action2;
    public static int action3;

    public void loadDeafult() {
        up = Keys.W;
        right = Keys.D;
        left = Keys.A;
        down = Keys.S;

        attackUp = Keys.UP;
        attackRight = Keys.RIGHT;
        attackLeft = Keys.LEFT;
        attackDown = Keys.DOWN;

        action1 = Keys.SPACE;
        action2 = Keys.E;
        action3 = Keys.SHIFT_LEFT;
    }
}
