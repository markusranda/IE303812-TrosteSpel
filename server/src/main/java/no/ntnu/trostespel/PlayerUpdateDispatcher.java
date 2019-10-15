package no.ntnu.trostespel;


import no.ntnu.trostespel.entity.GameObject;

import java.util.concurrent.*;

/**
 * This class is responsible for queuing and demultiplexing incoming
 * updates, and dispathching them for processing.
 */
public class PlayerUpdateDispatcher {

    private ExecutorService processors;
    private long startTime = 0;
    private MasterGameState masterGameState;


    public PlayerUpdateDispatcher() {
        this.processors = Executors.newCachedThreadPool();
        GameState<PlayerState, GameObject> gameState = GameState.getInstance();
        masterGameState = new MasterGameState(gameState);
    }

    /**
     * queue an update
     *
     * @param actions the update to queue
     */
    public void dispatch(PlayerActions actions) {
        Future f = processCMD(actions);
        updateMaster(f);
    }

    private Future<PlayerState> processCMD(PlayerActions actions) {
        Future<PlayerState> f = null;
        if (actions != null) {
            startTime = System.currentTimeMillis();
            f = processors.submit(new PlayerUpdateProcessor(actions, startTime));
        }
        return f;
    }

    private void updateMaster(Future<PlayerState> f) {
        if (f != null) {
            try {
                PlayerState change = (PlayerState) f.get();
                masterGameState.update(change);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
