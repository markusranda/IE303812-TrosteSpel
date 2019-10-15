package no.ntnu.trostespel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameState <P, G> {

    public static final int playerSpeed = 100;

    public HashMap<Long, P> players;
    private List<G> entities;
    private boolean ack = false;

    public GameState() {
        entities = new ArrayList<>();
    }

    public List<G> getEntities() {
        return entities;
    }

    public boolean isAck() {
        return ack;
    }

    public void setAck(boolean ack) {
        this.ack = ack;
    }
}
