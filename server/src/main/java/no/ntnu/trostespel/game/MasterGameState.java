package no.ntnu.trostespel.game;

import no.ntnu.trostespel.state.GameState;
import no.ntnu.trostespel.state.MovableState;
import no.ntnu.trostespel.state.PlayerState;

import java.util.Collection;
import java.util.HashMap;

//TODO: make MasterGameState threadsafe
public class MasterGameState {

    private volatile GameState<PlayerState, MovableState> gameState;

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
            // should never happen
        }
        // TODO: update the state of the game here, checking collisions with projectiles, updating health etc

        // add new objects spawned by the players
        // to the gamestate
        Collection<PlayerState> players = gameState.players.values();
        for (PlayerState player : players) {
            gameState.getProjectileStateUpdates().addAll(player.getSpawnedObjects());
            player.resetSpawnedObjects();
        }

        // persist the newly created projectiles
        for (MovableState state: gameState.getProjectileStateUpdates()) {
            gameState.getProjectiles().put(state.getId(), state);
        }

        // update projectiles positions and check collisions etc . . .
        gameState.getProjectiles().forEach((k, v) -> {

        });

    }

    public void read() {

    }


    public GameState getGameState() {
        return this.gameState;
    }
}
