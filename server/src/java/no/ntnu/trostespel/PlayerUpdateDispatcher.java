package java.no.ntnu.trostespel;

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

    private IdentityMap<long, Queue<UserInputManagerModel>> players;

    ScheduledExecutorService gateKeeper;

    ExecutorService processors;


    public PlayerUpdateDispatcher() {
        this.players = new IdentityMap<long, Queue<UserInputManagerModel>>(Config.MAX_PLAYERS);
        processors = Executors.newCachedThreadPool();
        run();
    }

    /**
     * queue an update
     * @param actions the update to queue
     */
    public void queue(UserInputManagerModel actions) {
        long pid = actions.getPid();
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
                Iterator<Queue<UserInputManagerModel>> it = players.iterator();
                while (it.hasNext()) {
                    Queue actions = it.next();

                }
            }
        };
    }
}
