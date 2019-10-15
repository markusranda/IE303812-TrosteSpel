package no.ntnu.trostespel;

import java.util.ArrayList;

public class CircularArrayList<E> extends ArrayList<E> {
    private static final long serialVersionUID = 1L;

    private int currentIndex = 0;
    private final int maxSize;
    private final GameState dummySnapshot;

    public CircularArrayList(int maxSize) {
        this.maxSize = maxSize;
        // TODO: 15.10.2019 Create a dummySnapshot with only empty values
        dummySnapshot = null;
    }

    public void setAtCurrent(E e) {
        this.set(currentIndex, e);
    }

    public Object getPrevious() {
        if (currentIndex == 0) {
            return dummySnapshot;
        } else {
            return this.get(currentIndex - 1);
        }
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
