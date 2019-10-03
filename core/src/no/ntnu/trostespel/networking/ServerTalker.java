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

    public ServerTalker() {
        run();
    }

    private void run(){
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        manager = new UserInputManager(socket);
        ticker = Executors.newSingleThreadScheduledExecutor();
        ticker.scheduleAtFixedRate(something(), 0, 100000 / ServerConfig.TICKRATE, TimeUnit.MICROSECONDS);
    }

    private Runnable something() {
        return () -> {
            manager.sendInput();
        };
    }
}
