package no.ntnu.trostespel.model;

import no.ntnu.trostespel.state.GameState;
import no.ntnu.trostespel.state.MovableState;
import no.ntnu.trostespel.state.PlayerState;

import java.util.ArrayList;

public class CircularArrayList<E> extends ArrayList<E> {
    private static final long serialVersionUID = 1L;

    private int currentIndex = 0;
    private final int maxSize;
    private boolean firstRun = true;

    public CircularArrayList(int maxSize) {
        this.maxSize = maxSize;
        for (int i = 0; i < maxSize; i++) {
            GameState gameState = null;
            this.add((E) gameState);
        }
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setAtCurrent(E e) {
        this.set(currentIndex, e);
    }

    public Object getCurrent() {
        return get(currentIndex);
    }

    public boolean isFirstRun() {
        return firstRun;
    }

    public void setFirstRun(boolean firstRun) {
        this.firstRun = firstRun;
    }

    public void incrementCursor() {
        if (currentIndex == maxSize - 1) {
            currentIndex = 0;
        } else {
            currentIndex++;
        }
    }

}
