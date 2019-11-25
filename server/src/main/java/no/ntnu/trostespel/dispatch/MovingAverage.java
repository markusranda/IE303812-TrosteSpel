package no.ntnu.trostespel.dispatch;

public class MovingAverage {

    private int size;
    private double total = 0d;
    private int index = 0;
    private double samples[];

    public MovingAverage(int size) {
        this.size = size;
        samples = new double[size];
        for (int i = 0; i < size; i++) samples[i] = 1d;
    }

    public void accumulate() {
        samples[index] += 1;
        total += 1;
    }

    public void step() {
        if (++index == size) index = 0;
        total -= samples[index];
        samples[index] = 0;
    }

    public double getAverage() {
        return total / size;
    }
}