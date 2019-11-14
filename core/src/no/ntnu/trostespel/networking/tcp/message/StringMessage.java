package no.ntnu.trostespel.networking.tcp.message;

import java.util.Arrays;

public class StringMessage extends TCPMessage {

    private String[] args;


    public StringMessage(TCPEvent event) {
        super(event);
    }

    public StringMessage(TCPEvent event, String[] args) {
        super(event);
        this.args = args;
    }

    public int addMessage(String message) {
        if (args == null) {
            args = new String[]{message};
        } else {
            final int len = args.length;
            args = Arrays.copyOf(args, len + 1);
            args[len] = message;
        }
        return args.length;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

}
