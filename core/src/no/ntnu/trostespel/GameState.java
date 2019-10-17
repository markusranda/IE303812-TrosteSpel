package no.ntnu.trostespel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameState <P, M> {

    public static final float playerSpeed = 0.05f;

    public HashMap<Long, P> players;
    private HashMap<Long, M> projectiles;
//    private List<G> entities;
    private boolean ack = false;

    public GameState() {
//        entities = new ArrayList<>();
        projectiles = new HashMap<>();
        players = new HashMap<>();
    }

//    public List<G> getEntities() {
//        return entities;
//    }

    public HashMap<Long, M> getProjectiles() {
        return projectiles;
    }

    public boolean isAck() {
        return ack;
    }

    public void setAck(boolean ack) {
        this.ack = ack;
    }
}
