package no.ntnu.trostespel.game;

import no.ntnu.trostespel.state.GameState;
import no.ntnu.trostespel.state.MovableState;
import no.ntnu.trostespel.state.PlayerState;

import java.util.Queue;

class PlayerUpdater extends Updater {

    private GameState<PlayerState, MovableState> gameState;
    private long pid;
    private long tick;

    public PlayerUpdater(GameState gameState, long pid) {
        super(1);
        this.gameState = gameState;
        this.pid = pid;
    }

    private void putProjectile ( long k, MovableState v){
        gameState.getProjectilesStateUpdates().add(v);
        gameState.getProjectiles().put(k, v);
    }

    private void doUpdate() {
        PlayerState player = gameState.players.get(pid);
        // add new objects spawned by the players
        // to the gamestate

        // apply new projectilestateupdates to the gamestate
        Queue<MovableState> newObjects = player.getSpawnedObjects();
        while (!player.getSpawnedObjects().isEmpty()) {
            MovableState newMovable = newObjects.poll();
            if (newMovable != null) {
                newMovable.setPosition(player.getPosition());
                putProjectile(newMovable.getId(), newMovable);
            }
        }
    }

    @Override
    public void run() {
        doUpdate();
    }
}