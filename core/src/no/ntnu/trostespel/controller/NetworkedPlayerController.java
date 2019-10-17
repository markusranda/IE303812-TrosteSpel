package no.ntnu.trostespel.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import no.ntnu.trostespel.GameState;
import no.ntnu.trostespel.config.PlayerKeyConfig;
import no.ntnu.trostespel.entity.Player;

public class NetworkedPlayerController extends ObjectController {

    private GameState gameState;
    private long pid;

    public NetworkedPlayerController(GameState gameState, long pid) {
        displacement = new Vector2();
        this.gameState = gameState;
        this.pid = pid;
    }

    @Override
    public Vector2 update(float delta) {
        return null;
    }
}
