package no.ntnu.trostespel.networking.tcp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import no.ntnu.trostespel.config.CommunicationConfig;
import no.ntnu.trostespel.entity.Session;
import no.ntnu.trostespel.networking.tcp.message.*;

import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

public class ConnectionClient {

    private static ConnectionClient instance = null;
    private Gson gson;
    private Set<MessageReceivedListener> listeners;
    private InetAddress serverAddress;
    private int serverPort;
    private BlockingQueue<TCPMessage> outQueue;
    private ExecutorService executor;
    private final Object MONITOR = new Object();

    private volatile boolean running = false;

    private ConnectionClient() {
        executor = Executors.newFixedThreadPool(2);
        outQueue = new LinkedBlockingQueue<>();
        listeners = new HashSet<>();

        // the following code allows deserializing classes which inherits some base class,
        // making tcp messaging easy kill
        // see https://stackoverflow.com/questions/21767485/gson-deserialization-to-specific-object-type-based-on-field-value
        gson = CommunicationConfig.getGsonForTcp();
    }

    public static ConnectionClient getInstance() {
        if (instance == null) {
            instance = new ConnectionClient();
        }
        return instance;
    }

    public ConnectionClient bind(InetAddress serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        return this;
    }

    public void start() {
        //executor.execute(senderRunnable());
        //executor.execute(receiverRunnable());
        executor.execute(tcpRunner());
    }

    /**
     * core logic of the TCP client
     * blocks on send with a timeout
     * does NOT block while waiting for incoming data
     * @return
     */
    private Runnable tcpRunner() {
        return () -> {
            running = true;
            while (running) {
                Socket socket = null;
                try {
                    socket = new Socket(serverAddress, serverPort);
                    InputStream inputStream = socket.getInputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                    // Send
                    // wait up to one second for an outgoing message
                    // on timeout, the method continues and checks for incoming messages
                    TCPMessage msg = outQueue.poll(1000, TimeUnit.MILLISECONDS);
                    if (msg != null) {
                        String json = serialize(msg);
                        out.println(json);
                    }

                    // Receive
                    if (inputStream.available() < 2) {
                        String receivedString = in.readLine();
                        TCPMessage received = deserialize(receivedString);
                        notifyListeners(received);
                    }

                } catch (InterruptedException | IOException ex) {
                    ex.printStackTrace();
                } finally {
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
    }


    private TCPMessage deserialize(String message) {
        try {
            TCPMessage result = gson.fromJson(message, TCPMessage.class);
            return result;
        } catch (JsonSyntaxException | IllegalStateException e) {
            e.printStackTrace();
            System.out.println("man what");
        }
        return null;
    }

    private String serialize(TCPMessage message) {
        return gson.toJson(message, TCPMessage.class);
    }

    public boolean sendMessage(TCPMessage message) {
        return outQueue.offer(message);
    }

    public void shutdown() {
        this.running = false;
    }

    /**
     * tries to create a socket with given port
     * otherwise, creates a socket with wildcard port
     *
     * @param port
     * @return
     */
    public DatagramSocket createUDPSocket(int port) {
        DatagramSocket udpSocket = null;
        try {
            if (port == 0) {
                udpSocket = new DatagramSocket();
            } else {
                udpSocket = new DatagramSocket(port);
            }
        } catch (SocketException e) {
            System.out.println("Couldnt bind UDP socket to selected port . . .");
            try {
                udpSocket = new DatagramSocket();
            } catch (SocketException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return udpSocket;
    }

    private void notifyListeners(TCPMessage msg) {
        Set<MessageReceivedListener> listenersCopy;

        synchronized (MONITOR) {
            if (listeners == null) return;
            listenersCopy = new HashSet<>(listeners);
        }

        for (MessageReceivedListener listener : listenersCopy) {
            listener.onReceive(msg);
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void listen(MessageReceivedListener listener) {
        if (listener == null) return;
        synchronized (MONITOR) {
            listeners.add(listener);
        }
    }

    public void stopListen(MessageReceivedListener listener) {
        if (listener == null) return;
        synchronized (MONITOR) {
            listeners.remove(listener);
        }
    }

    public interface MessageReceivedListener {
        void onReceive(TCPMessage msg);
    }
}
