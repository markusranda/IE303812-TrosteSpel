package no.ntnu.trostespel.entity;


public class Session {

    private static final Session instance = new Session();

    private long playerID = 0;

    private Session() {
    }

    public static Session getInstance() {
        return instance;
    }

    public long getPlayerID() {
        return playerID;
    }

    /**
     * Tries to set the playerID for this session.
     *
     * @param playerID The playerID
     * @return True if playerID hasn't been set yet, false otherwise
     */
    public boolean setPlayerID(long playerID) {
        if (playerID == 0){
            this.playerID = playerID;
            return true;
        }
        return false;
    }
}
