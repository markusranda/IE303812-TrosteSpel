package no.ntnu.trostespel.game;

public abstract class Updater implements Runnable {
    private int importance;

    protected Updater(int importance) {
        this.importance = importance;
    }

    public int getImportance() {
        return importance;
    }
}
