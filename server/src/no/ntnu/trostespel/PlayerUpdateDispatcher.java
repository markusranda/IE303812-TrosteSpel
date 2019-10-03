package no.ntnu.trostespel;


import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Queue;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class is responsible for queuing and demultiplexing incoming
 * updates, and dispathching them for processing.
 */
public class PlayerUpdateDispatcher {

    private IdentityMap<Long, Queue<UserInputManagerModel>> players;

    ScheduledExecutorService gateKeeper;

    ExecutorService processors;


    public PlayerUpdateDispatcher() {
        this.players = new IdentityMap<Long, Queue<UserInputManagerModel>>(Config.MAX_PLAYERS);
        processors = Executors.newCachedThreadPool();
        run();
    }

    /**
     * queue an update
     * @param actions the update to queue
     */
    public void queue(UserInputManagerModel actions) {
        long pid = actions.pid;
        if (!players.containsKey(pid)) {
            Queue<UserInputManagerModel> queue = new Queue<UserInputManagerModel>();
            queue.addFirst(actions);
            players.put(pid, queue);
        } else {
            players.get(pid).addLast(actions);
        }
    }

    /**
     * Run a timed dispathcer
     */
    private void run() {
        //TODO: BLOCK "players" when dispatching
        gateKeeper = Executors.newSingleThreadScheduledExecutor();
        gateKeeper.scheduleAtFixedRate(something(), 0, 100000 / Config.TICKRATE, TimeUnit.MICROSECONDS);

    }

    private Runnable something() {
        return new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                Iterator<ObjectMap.Entry<Long, Queue<UserInputManagerModel>>> it = players.iterator();
                while (it.hasNext()) {
                    System.out.println("Running...");
                    ObjectMap.Entry<Long, Queue<UserInputManagerModel>> actions = it.next();
                    //actions.value

                }
            }
        };
    }
}
