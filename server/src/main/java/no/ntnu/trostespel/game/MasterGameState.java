package no.ntnu.trostespel.game;

import com.badlogic.gdx.math.Vector2;
import no.ntnu.trostespel.GameState;
import no.ntnu.trostespel.model.Connections;
import no.ntnu.trostespel.state.MovableState;
import no.ntnu.trostespel.state.PlayerState;
import no.ntnu.trostespel.entity.GameObject;

public class MasterGameState {

    private GameState<PlayerState, GameObject, MovableState> gameState;

    private static MasterGameState single_instance = null;

    public static MasterGameState getInstance() {
        if (single_instance == null) {
            single_instance = new MasterGameState(new GameState());
        }
        return single_instance;
    }

    private MasterGameState(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     *
     * @param pid the id of the player to update
     * @param stateChange a PlayerState object containing only relative values
     */
    public void update(long pid, PlayerState stateChange) {
        PlayerState playerState = gameState.players.get(pid);
        if (playerState == null) {
            // add player to to game
            final int START_HEALTH = 100;
            final Vector2 SPAWN_POS = new Vector2(100, 100);
            PlayerState newPlayer = new PlayerState(pid, SPAWN_POS, START_HEALTH);
            playerState = gameState.players.put(pid, newPlayer);
        }

        // update the state of the player
        gameState.players.put(pid, playerState.update(stateChange));
    }

    public void read() {

    }

    public GameState getGameState() {
        return this.gameState;
    }
}
