package no.ntnu.trostespel.entity;


import no.ntnu.trostespel.state.GameState;
import no.ntnu.trostespel.state.MovableState;
import no.ntnu.trostespel.state.PlayerState;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Session {

    private static final Session instance = new Session();

    private long playerID = 0;

    private volatile GameState<PlayerState, MovableState> receivedGameState;

    // syncronization
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    private Lock readLock = lock.readLock();
    private Lock writeLock = lock.writeLock();

    // RTT timers
    private long packetSendTime = 0;
    private long packetReceiveTime = 0;
    private String username;

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
        if (this.playerID == 0){
            this.playerID = playerID;
            return true;
        }
        return false;
    }

    public void setReceivedGameState(GameState<PlayerState, MovableState> receivedGameState) {
        writeLock.lock();
        try {
            this.receivedGameState = receivedGameState;
        } finally {
            writeLock.unlock();
        }
    }

    public GameState<PlayerState, MovableState> getReceivedGameState() {
        readLock.lock();
        GameState<PlayerState, MovableState> copy = receivedGameState;
        try {
            return copy;
        } finally {
            readLock.unlock();
        }
    }

    public long getPacketSendTime() {
        return packetSendTime;
    }

    public long getPacketReceiveTime() {
        return packetReceiveTime;
    }

    public void setPacketSendTime(long packetSendTime) {
        this.packetSendTime = packetSendTime;
    }

    public void setPacketReceiveTime(long packetReceiveTime) {
        this.packetReceiveTime = packetReceiveTime;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }
}
