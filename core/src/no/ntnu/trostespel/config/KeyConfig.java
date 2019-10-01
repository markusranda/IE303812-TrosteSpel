package no.ntnu.trostespel.config;

import com.badlogic.gdx.Input.Keys;

public class KeyConfig extends Config {

    //---player controls---
    public int up;
    public int right;
    public int left;
    public int down;
    public int attack;
    public int defend;

    public int attackUp;
    public int attackRight;
    public int attackLeft;
    public int attackDown;

    public int action1;
    public int action2;
    public int action3;

    //---UI controls---
    public int toggleHUD;

    //---screen controls---
    public int fullscreen;
    public int vsync;

    //---debug menu controls---
    public int toggleDebug;
    public int togglePos;
    public int toggleComponents;
    public int toggleBounds;
    public int toggleFPS;
    public int toggleVector;
    public int toggleMenu;

    public void loadDefault() {
        //player
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


        //ui
        toggleHUD = Keys.H;

        //screen
        fullscreen = Keys.F11;
        vsync = Keys.F8;

        //debug menu
        toggleDebug = Keys.F3;
        togglePos = Keys.NUMPAD_0;
        toggleComponents = Keys.NUMPAD_1;
        toggleBounds = Keys.NUMPAD_2;
        toggleFPS = Keys.NUMPAD_3;
        toggleVector = Keys.NUMPAD_5;
        toggleMenu = Keys.NUMPAD_9;
    }

}