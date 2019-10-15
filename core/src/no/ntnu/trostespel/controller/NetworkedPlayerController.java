package no.ntnu.trostespel.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import no.ntnu.trostespel.GameState;
import no.ntnu.trostespel.config.PlayerKeyConfig;

public class NetworkedPlayerController extends ObjectController {

    private GameState gameState;

    public NetworkedPlayerController(GameState gameState, long pid) {
        displacement = new Vector2();
        this.gameState = gameState;
    }

    @Override
    public Vector2 update(float delta) {
        displacement.setZero();
        return displacement;
    }
}
