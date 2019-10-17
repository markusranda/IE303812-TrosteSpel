package no.ntnu.trostespel.networking;

import no.ntnu.trostespel.config.CommunicationConfig;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameDataTransmitter {

    private ScheduledExecutorService ticker;
    private UserInputManager manager;
    private DatagramSocket socket;
    private Runnable emitter;

    public GameDataTransmitter(long pid) {
        ticker = Executors.newSingleThreadScheduledExecutor();
        this.emitter = emitter();
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        manager = new UserInputManager(socket);
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
