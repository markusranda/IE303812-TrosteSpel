package no.ntnu.trostespel.networking;

public abstract class CommunicationManager implements ServerConnector {

    @Override
    public int test1() {
        return 0;
    }

    @Override
    public int test2() {
        return 0;
    }

    @Override
    public boolean sendUserInput() {
        return false;
    }
}
