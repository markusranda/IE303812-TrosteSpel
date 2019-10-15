package no.ntnu.trostespel;

public class MasterGameState {

    private GameState gameState;

    public MasterGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void update(PlayerState stateChange) {
        //PlayerState oldState = gameState.players.get()
        //gameState.players.put()
    }

    public GameState read() {
        return null;
    }
}
