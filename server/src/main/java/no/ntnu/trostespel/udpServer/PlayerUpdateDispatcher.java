package no.ntnu.trostespel.udpServer;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import no.ntnu.trostespel.PlayerActions;
import no.ntnu.trostespel.game.MasterGameState;
import no.ntnu.trostespel.state.PlayerState;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * This class is responsible for queuing and demultiplexing incoming
 * updates, and dispathching them for processing.
 */
public class PlayerUpdateDispatcher extends ThreadPoolExecutor {

    private MasterGameState masterGameState;

    private Map<Long, Long> workers;

    public PlayerUpdateDispatcher() {
        super(1, 8, 0, TimeUnit.HOURS, new LinkedBlockingQueue<>(8),
                new ThreadFactoryBuilder().setNameFormat("Dispathcher-thread-%d").build());
        masterGameState = MasterGameState.getInstance();
        this.workers = new HashMap<>();
    }

    /**
     * dispatch actions for processing and update
     * masterGameState
     *
     * @param actions the update to queue
     */
    public void dispatch(PlayerActions actions) {
        // each player only gets one update per tick
        // excess updates get discarded
        long currentTick = GameServer.getTickcounter();
        long pid = actions.pid;
        if (!workers.containsKey(pid)) {
            workers.put(actions.pid, currentTick - 1);
        }

        if (workers.get(pid) < currentTick) {
            executeCMD(actions, currentTick);
        } else {
        }
    }


    private void executeCMD(PlayerActions actions, long startTime) {
        PlayerState playerState = (PlayerState) masterGameState.getGameState().players.get(actions.pid);
        if (playerState == null) {
            playerState = new PlayerState(actions.pid);
            masterGameState.getGameState().players.put(actions.pid, playerState);
        }
        PlayerUpdateProcessor processor = new PlayerUpdateProcessor(playerState, actions, startTime);
        workers.put(processor.getPid(), startTime);
        execute(processor);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (r instanceof PlayerUpdateProcessor) {
            long pid = ((PlayerUpdateProcessor) r).getPid();
            updateMaster(pid);
        }
    }

    private void updateMaster(long pid) {
        masterGameState.update(pid);
    }
}
