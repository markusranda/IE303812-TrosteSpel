package no.ntnu.trostespel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameState <P, G, M> {

    public static final int playerSpeed = 100;

    public HashMap<Long, P> players;
    private HashMap<Long, M> projectiles;
    private List<G> entities;

    // static variable single_instance of type Singleton
    private static GameState single_instance = null;

    public static GameState getInstance() {
        if (single_instance == null) {
            single_instance = new GameState();
        }
        return single_instance;
    }

    private GameState() {
        entities = new ArrayList<>();
        projectiles = new HashMap<>();
    }

    public List<G> getEntities() {
        return entities;
    }

    public HashMap<Long, M> getProjectiles() {
        return projectiles;
    }
}
