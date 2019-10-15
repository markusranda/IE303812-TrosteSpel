package helper;

import no.ntnu.trostespel.PlayerUpdateDispatcher;
import no.ntnu.trostespel.config.ServerConfig;

import java.net.DatagramSocket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DummyServerTalker {

    private ScheduledExecutorService ticker;
    private DummyUserInputManager manager;
    private DatagramSocket socket;
    private Runnable emitter;
    PlayerUpdateDispatcher dispatcher;

    public DummyServerTalker(PlayerUpdateDispatcher dispatcher) {
        this.emitter = getEmitter();
        this.dispatcher = dispatcher;
        this.manager = new DummyUserInputManager();
        run();
    }

    private void run(){
        ticker = Executors.newSingleThreadScheduledExecutor();
        ticker.scheduleAtFixedRate(emitter, 0, 1000000 / ServerConfig.TICKRATE, TimeUnit.MICROSECONDS);
    }

    private Runnable getEmitter() {
        return  new Runnable() {
            @Override
            public void run() {
                dispatcher.dispatch(manager.getRandomInput());
            }
        };
    }
}
