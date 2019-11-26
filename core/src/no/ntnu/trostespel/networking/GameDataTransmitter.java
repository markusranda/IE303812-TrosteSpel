package no.ntnu.trostespel.networking;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.entity.Movable;
import no.ntnu.trostespel.entity.Player;
import no.ntnu.trostespel.state.GameState;

import java.net.DatagramSocket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameDataTransmitter {

    private ScheduledExecutorService ticker;
    private UserInputManager manager;
    private Runnable emitter;

    public GameDataTransmitter(DatagramSocket socket, long pid, GameState<Player, Movable> gameState) {
        ticker = Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryBuilder().setNameFormat("GameDataTransmitter-%d").build());

        this.emitter = emitter();
        manager = new UserInputManager(socket, gameState);
        manager.setPid(pid);
        run();
    }

    public void run() {
        ticker.scheduleAtFixedRate(emitter, 0, 1000000000  / (CommunicationConfig.TICKRATE), TimeUnit.NANOSECONDS);
    }


    public void stop() {
        if (!ticker.isTerminated()) {
            ticker.shutdown();
        }
    }

    private Runnable emitter() {
        return () -> {
            manager.sendInput();
        };
    }
}
