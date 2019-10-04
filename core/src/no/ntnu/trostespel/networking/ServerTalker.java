package no.ntnu.trostespel.networking;

import no.ntnu.trostespel.ServerConfig;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerTalker {

    private ScheduledExecutorService ticker;
    private UserInputManager manager;
    private DatagramSocket socket;
    private Runnable emitter;

    public ServerTalker() {
        ticker = Executors.newSingleThreadScheduledExecutor();
        this.emitter = emitter();
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        manager = new UserInputManager(socket);
        run();
    }

    private void run(){
        ticker.scheduleAtFixedRate(emitter, 0, 100000 / ServerConfig.TICKRATE, TimeUnit.MICROSECONDS);
    }

    private Runnable emitter() {
        return () -> {
            manager.sendInput();
        };
    }
}
