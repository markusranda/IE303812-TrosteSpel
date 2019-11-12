package no.ntnu.trostespel.networking;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import no.ntnu.trostespel.PlayerActions;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.entity.Movable;
import no.ntnu.trostespel.entity.Player;
import no.ntnu.trostespel.state.GameState;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameDataTransmitter {

    private ScheduledExecutorService ticker;
    private UserInputManager manager;
    private Runnable emitter;
    private Serializer<PlayerActions> serializer;

    private int length;
    private DatagramPacket packet;
    private DatagramSocket socket;

    public GameDataTransmitter(DatagramSocket socket, long pid, GameState<Player, Movable> gameState) {
        ticker = Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryBuilder().setNameFormat("GameDataTransmitter-%d").build());

        serializer = new Serializer<>(PlayerActions.class);
        this.socket = socket;
        this.emitter = emitter();
        manager = new UserInputManager(gameState);
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
            PlayerActions cmd = manager.getCmd();
            byte[] buf = serializer.writeAndCopyBuffer(cmd);
            serializer.flush();
            packet = new DatagramPacket(
                    buf,
                    buf.length,
                    CommunicationConfig.host,
                    CommunicationConfig.SERVER_UDP_GAMEDATA_RECEIVE_PORT);
            try {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }
}
