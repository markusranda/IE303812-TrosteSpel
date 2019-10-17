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
public class PlayerUpdateDispatcher extends ThreadPoolExecutor {

    private long startTime = 0;
    private MasterGameState masterGameState;




    public PlayerUpdateDispatcher() {
        super(8, 8, 0, TimeUnit.HOURS, new LinkedBlockingQueue<>(24));
        masterGameState = MasterGameState.getInstance();
    }

    /**
     * dispatch actions for processing and update
     * masterGameState
     *
     * @param actions the update to queue
     */
    public void dispatch(PlayerActions actions) {
        processCMD(actions);
        updateMaster(actions.pid);
    }

    private void processCMD(PlayerActions actions) {
        startTime = System.currentTimeMillis();

        PlayerState playerState = (PlayerState) masterGameState.getGameState().players.get(actions.pid);
        if (playerState == null) {
            playerState = new PlayerState(actions.pid);
            masterGameState.getGameState().players.put(actions.pid, playerState);
        }
        PlayerUpdateProcessor processor = new PlayerUpdateProcessor(playerState, actions, startTime);
        execute(processor);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        //remove(r);
    }

    private void updateMaster(long pid) {
        masterGameState.update(pid);
    }
}
