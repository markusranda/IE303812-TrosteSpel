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

    public ServerTalker(long pid) {
        ticker = Executors.newSingleThreadScheduledExecutor();
        this.emitter = emitter();
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        manager = new UserInputManager(socket);
        manager.setPid(pid);
    }

    public void run(){
        if (ticker.isTerminated()) {
            ticker.scheduleAtFixedRate(emitter, 0, 100000 / ServerConfig.TICKRATE, TimeUnit.MICROSECONDS);
        }
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
