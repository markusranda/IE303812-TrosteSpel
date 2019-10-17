package no.ntnu.trostespel.state;

import no.ntnu.trostespel.config.CommunicationConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameState<P, M> {

    public static double playerSpeed = 300d / CommunicationConfig.TICKRATE;

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

    public HashMap<Long, M> addProjectiles(HashMap<Long, M> map) {
        Map mergedMap = Stream.concat(projectiles.entrySet().stream(),
                map.entrySet().stream())
                .collect(Collectors.toMap(
                        longEntry -> longEntry.getKey(),
                        longEntry -> longEntry.getValue()
                ));
        this.projectiles = (HashMap<Long, M>) mergedMap;
        return this.projectiles;
    }
}
