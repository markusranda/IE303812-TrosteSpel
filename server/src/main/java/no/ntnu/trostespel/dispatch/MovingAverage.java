package no.ntnu.trostespel.dispatch;

public class MovingAverage {

    private int size;
    private double total = 0d;
    private int index = 0;
    private double samples[];

    public MovingAverage(int size) {
        this.size = size;
        samples = new double[size];
        for (int i = 0; i < size; i++) samples[i] = 0d;
    }

    public void accumulate(double x) {
        samples[index] += x;
        total += x;
    }

    public void step() {
        total -= samples[index];
        if (++index == size) index = 0; // cheaper than modulus
        samples[index] = 0;
    }

    public double getAverage() {
        return total / size;
    }
}