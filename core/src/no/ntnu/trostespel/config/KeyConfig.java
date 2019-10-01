package no.ntnu.trostespel.config;

import com.badlogic.gdx.Input.Keys;

public class KeyConfig extends PlayerKeyConfig {

    //---player controls---

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

    public int[] remoteCommands;

    public void loadDefault() {
        super.loadDeafult();
        //player
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