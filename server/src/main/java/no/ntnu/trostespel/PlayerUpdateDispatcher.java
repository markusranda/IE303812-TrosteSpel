package no.ntnu.trostespel;


import com.badlogic.gdx.utils.Queue;
import no.ntnu.trostespel.config.ServerConfig;

import java.util.*;
import java.util.concurrent.*;

/**
 * This class is responsible for queuing and demultiplexing incoming
 * updates, and dispathching them for processing.
 */
public class PlayerUpdateDispatcher {

    private ExecutorService processors;
    private long startTime = 0;


    public PlayerUpdateDispatcher() {
        this.processors = Executors.newCachedThreadPool();
    }

    /**
     * queue an update
     *
     * @param actions the update to queue
     */
    public void queue(PlayerActions actions) {
        if (actions != null) {
            startTime = System.currentTimeMillis();
            Future f = processors.submit(new PlayerUpdateProcessor(actions, startTime));
        }
    }

}
