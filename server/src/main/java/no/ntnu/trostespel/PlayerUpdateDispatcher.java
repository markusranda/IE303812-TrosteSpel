package no.ntnu.trostespel;


import no.ntnu.trostespel.game.MasterGameState;
import no.ntnu.trostespel.game.PlayerUpdateProcessor;
import no.ntnu.trostespel.state.PlayerState;

import java.util.HashSet;
import java.util.Set;
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
        masterGameState = MasterGameState.getInstance();
    }

    /**
     * dispatch actions for processing and update
     * masterGameState
     *
     * @param actions the update to queue
     */
    public void dispatch(PlayerActions actions) {
        Future<PlayerState> f = processCMD(actions);
        updateMaster(f);
    }

    private Future<PlayerState> processCMD(PlayerActions actions) {
        startTime = System.currentTimeMillis();
        PlayerState playerState = (PlayerState) masterGameState.getGameState().players.get(actions.pid);
        return processors.submit(new PlayerUpdateProcessor(playerState, actions, startTime));
    }

    private void updateMaster(Future<PlayerState> f) {
        if (f != null) {
            try {
                PlayerState change = f.get();
                long pid = change.getPid();
                // TODO: this action should be performed only once the PlayerUpdateProcessor is done
                // TODO: find a good way to synchronize these actions :^ )
                masterGameState.update(pid);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
