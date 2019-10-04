package no.ntnu.trostespel;


import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Queue;

import java.util.*;
import java.util.concurrent.*;

/**
 * This class is responsible for queuing and demultiplexing incoming
 * updates, and dispathching them for processing.
 */
public class PlayerUpdateDispatcher {

    private Map<Long, Queue<PlayerActions>> players; //TODO: fix concurrent access

    private ScheduledExecutorService gateKeeper;
    private ExecutorService processors;
    private Runnable dispatcher;


    public PlayerUpdateDispatcher() {
        this.players = new HashMap<>();
        this.processors = Executors.newCachedThreadPool();
        this.dispatcher = dispatcher();
        run();
    }

    /**
     * queue an update
     *
     * @param actions the update to queue
     */
    public void queue(PlayerActions actions) {
        if (actions != null) {

            long pid = actions.pid;
            try {
                if (!players.containsKey(pid)) {
                    Queue<PlayerActions> queue = new Queue<PlayerActions>();
                    queue.addFirst(actions);
                    players.put(pid, queue);
                } else {
                    players.get(pid).addLast(actions);
                }
            } catch (Exception e) {
                System.out.println("Queue failure");
                e.printStackTrace();
            }
        }
    }

    /**
     * Run a timed dispathcer
     */
    private void run() {
        //TODO: BLOCK "players" when dispatching
        gateKeeper = Executors.newSingleThreadScheduledExecutor();
        System.out.println(10000 / ServerConfig.TICKRATE);
        gateKeeper.scheduleAtFixedRate(dispatcher, 0, 100000 / ServerConfig.TICKRATE, TimeUnit.MICROSECONDS);

    }

    private Runnable dispatcher() {
        return () -> {
            try {
                long startTime = System.currentTimeMillis();
                Iterator<Map.Entry<Long, Queue<PlayerActions>>> it = players.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Long, Queue<PlayerActions>> actions = it.next();
                    processors.submit(new PlayerUpdateProcessor(actions.getValue(), startTime));
                    it.remove();
                }
            } catch (ConcurrentModificationException e) {
                e.printStackTrace();
            }
        };
    }
}
