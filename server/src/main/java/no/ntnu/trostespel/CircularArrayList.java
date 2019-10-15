package no.ntnu.trostespel;

import java.util.ArrayList;

public class CircularArrayList<E> extends ArrayList<E> {
    private static final long serialVersionUID = 1L;

    private int currentIndex = 0;
    private final int maxSize;
    private boolean firstRun = true;

    public CircularArrayList(int maxSize) {
        this.maxSize = maxSize;
    }

    public void setAtCurrent(E e) {
        this.set(currentIndex, e);
    }

    public Object getCurrent() {
        if (firstRun) {
            firstRun = false;
            return null;
        }
        return get(currentIndex);
    }

    public Object getNext() {
        Object obj = this.get(currentIndex);
        if (currentIndex == maxSize - 1) {
            currentIndex = 0;
        } else {
            currentIndex++;
        }
        return obj;
    }
}
