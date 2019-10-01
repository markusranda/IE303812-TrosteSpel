package no.ntnu.trostespel.networking;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient implements Runnable{

    public InetAddress getInetAddress() {
        return socket.getInetAddress();
    }

    private Socket socket;
    private Scanner scanner;
    TCPClient(InetAddress serverAddress, int serverPort) throws Exception {
        this.socket = new Socket(serverAddress, serverPort);
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        System.out.println("\r\nConnected to Server: " + getInetAddress());
        String msg = "Markus";

        try {
            PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
            out.println(msg);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


//        String input;
//
//        try {
//            while (true) {
//                input = scanner.nextLine();
//                PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
//                out.println(input);
//                out.flush();
//            }
//        } catch (IOException ie) {
//            ie.printStackTrace();
//        }
    }



}
