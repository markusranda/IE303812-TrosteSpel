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

    private long startTime = 0;


    public PlayerUpdateDispatcher() {
        this.players = new ConcurrentHashMap<>();
        this.processors = Executors.newCachedThreadPool();
        this.dispatcher = getDispatcher();
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
                } else if (players.containsKey(pid)) {
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
        gateKeeper.scheduleAtFixedRate(dispatcher, 0, 1000000 / ServerConfig.TICKRATE, TimeUnit.MICROSECONDS);
    }

    private int count = 0;
    private int period = 300;
    private Runnable getDispatcher() {
        return () -> {
            /*count++;
            if (count >= period) {
                count = 0;
                long time = System.currentTimeMillis() - startTime;
                System.out.println("dispatched " + time / period);
                startTime = System.currentTimeMillis();
            }*/
            try {
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
