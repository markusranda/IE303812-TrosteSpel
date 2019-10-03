package no.ntnu.trostespel;

import com.badlogic.gdx.utils.Queue;

public class PlayerUpdateProcessor implements Runnable {

    private Queue<UserInputManagerModel> actions;
    private long startTime;
    private long delta;

    public PlayerUpdateProcessor(Queue<UserInputManagerModel> actions, long startTime) {
        this.actions = actions;
        this.startTime = startTime;
    }

    @Override
    public void run() {
        System.out.println("I be working nigga");
        delta = startTime - System.currentTimeMillis();
    }
}
