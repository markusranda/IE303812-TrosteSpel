package no.ntnu.trostespel.networking;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import no.ntnu.trostespel.config.CommunicationConfig;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameDataTransmitter {

    private ScheduledExecutorService ticker;
    private UserInputManager manager;
    private Runnable emitter;

    public GameDataTransmitter(DatagramSocket socket, long pid, TiledMap tiledMap) {
        ticker = Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryBuilder().setNameFormat("GameDataTransmitter-%d").build());

        this.emitter = emitter();
        manager = new UserInputManager(socket, tiledMap);
        manager.setPid(pid);
        run();
    }

    public void run() {
        ticker.scheduleAtFixedRate(emitter, 0, 1000 / CommunicationConfig.TICKRATE, TimeUnit.MILLISECONDS);
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
