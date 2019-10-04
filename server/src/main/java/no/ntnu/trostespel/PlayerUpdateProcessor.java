package no.ntnu.trostespel;

import com.badlogic.gdx.utils.Queue;

public class PlayerUpdateProcessor implements Runnable {

    private Queue<PlayerActions> actions;
    private long startTime;
    private long delta;

    public PlayerUpdateProcessor(Queue<PlayerActions> actions, long startTime) {
        this.actions = actions;
        this.startTime = startTime;
    }

    @Override
    public void run() {
        delta = startTime - System.currentTimeMillis();
    }
}
