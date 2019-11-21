package no.ntnu.trostespel;

import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.model.Connections;
import org.apache.logging.log4j.LogManager;

public class GameServerStats {

    private static final String tag = "GameServer";
    private static long lastTime;
    private static float accumulatedDelta;

    public static void measureJitter() {
        long timeSinceLast = System.currentTimeMillis() - lastTime;
        float expected = 1000f / CommunicationConfig.TICKRATE;
        float delta = expected - timeSinceLast;
        accumulatedDelta += delta;
        LogManager.getLogger(tag).trace("Timestep complete. Connected players: " + Connections.getInstance().getConnections().size() +
                "TimeSinceLast: " + timeSinceLast +
                " Delta: " + delta +
                " Expected " + expected +
                " Accumulated " + accumulatedDelta);
        lastTime = System.currentTimeMillis();
        if (delta > 3) {
            System.out.println("GameServer tick() WARN: High jitter: " + delta + "ms");
        }
    }
}
