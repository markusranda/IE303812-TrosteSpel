package no.ntnu.trostespel.entity;


import com.google.common.collect.EvictingQueue;
import no.ntnu.trostespel.state.GameState;
import no.ntnu.trostespel.state.MovableState;
import no.ntnu.trostespel.state.PlayerState;

import java.net.DatagramSocket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Session {

    private static final Session instance = new Session();

    private long pid = 0;

    private GameState<PlayerState, MovableState> receivedGameState;

    // syncronization
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    private Lock readLock = lock.readLock();
    private Lock writeLock = lock.writeLock();

    /**
     * This queue will contain the five last seqNums, in other words it will contain the five last
     * commands the client sent to the server
     */
    private final int MAX_SIZE_SEQ_NUMS = 5;
    private EvictingQueue<Long> seqNums = EvictingQueue.create(MAX_SIZE_SEQ_NUMS);
    private ReentrantReadWriteLock seqNumLock = new ReentrantReadWriteLock();
    private Lock seqNumWriteLock = seqNumLock.writeLock();
    private Lock seqNumReadLock = seqNumLock.readLock();

    /**
     * Adds a new seqNum to the EvictingQueue. This method implements
     * a write lock, so it's considered thread-safe.
     *
     * @param seqNum the sequence number to add
     */
    public void addSeqNum(long seqNum) {
        seqNumWriteLock.lock();
        try {
            seqNums.add(seqNum);
        } finally {
            seqNumWriteLock.unlock();
        }
    }

    /**
     * Retrieves all the seqNums
     */
    public EvictingQueue<Long> getSeqNums() {
        seqNumReadLock.lock();
        try {
            return seqNums;
        } finally {
            seqNumReadLock.unlock();
        }
    }

    private String username;
    private DatagramSocket udpSocket;
    private String mapFileName;

    private Session() {
    }

    public static Session getInstance() {
        return instance;
    }

    public long getPid() {
        return pid;
    }

    /**
     * Tries to set the playerID for this session.
     *
     * @param playerID The playerID
     * @return True if playerID hasn't been set yet, false otherwise
     */
    public boolean setPid(long playerID) {
        if (this.pid == 0) {
            this.pid = playerID;
            return true;
        }
        return false;
    }

    public void killConnection() {
        this.pid = 0;
        this.udpSocket = null;
        this.username = null;
        this.mapFileName = null;
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
        writeLock.lock(); // todo should this be a readlock? NOTICE ME SEMPAI IN PULL REQUEST PLEASE
        GameState<PlayerState, MovableState> copy = receivedGameState;
        try {
            return copy;
        } finally {
            writeLock.unlock();
        }
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public DatagramSocket getUdpSocket() {
        return udpSocket;
    }

    public void setUdpSocket(DatagramSocket udpSocket) {
        this.udpSocket = udpSocket;
    }

    public void setMapName(String mapFileName) {
        this.mapFileName = mapFileName;
    }

    public String getMapFileName() {
        return mapFileName;
    }
}
