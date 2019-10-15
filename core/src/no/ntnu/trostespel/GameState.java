package no.ntnu.trostespel;

import no.ntnu.trostespel.entity.GameObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameState <P, G> {

    public static final int playerSpeed = 100;

    public HashMap<Long, P> players;
    private List<GameObject> entities;

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
    }

    public List<GameObject> getEntities() {
        return entities;
    }

    public void addEntity(GameObject gameObject) {
        getEntities().add(gameObject);
    }
}
