package no.ntnu.trostespel.game;

import com.badlogic.gdx.math.Vector2;
import no.ntnu.trostespel.GameState;
import no.ntnu.trostespel.state.MovableState;
import no.ntnu.trostespel.state.PlayerState;
import no.ntnu.trostespel.entity.GameObject;

//TODO: make MasterGameState threadsafe
public class MasterGameState {

    private GameState<PlayerState, MovableState> gameState;

    private static MasterGameState single_instance = null;

    public static MasterGameState getInstance() {
        if (single_instance == null) {
            single_instance = new MasterGameState(new GameState<>());
        }
        return single_instance;
    }

    private MasterGameState(GameState<PlayerState, MovableState> gameState) {
        this.gameState = gameState;
    }

    /**
     *
     * @param pid the id of the player to update
     */
    public void update(long pid) {
        if (!gameState.players.containsKey(pid)) {
            // add player to to game
            final int START_HEALTH = 100;
            final Vector2 SPAWN_POS = new Vector2(100, 100);
            PlayerState playerState = new PlayerState(pid, SPAWN_POS, START_HEALTH);
            gameState.players.put(pid, playerState);
        }
        // TODO: update the state of the game here, checking collisions with projectiles, updating health etc
    }

    public void read() {

    }

    public GameState getGameState() {
        return this.gameState;
    }
}
