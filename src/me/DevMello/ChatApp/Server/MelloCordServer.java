package me.DevMello.ChatApp.Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class MelloCordServer {

    private static final int PORT = 9005;

    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();

    public static void main(String[] args) throws Exception {
        System.out.println("MelloCord Server running...");
        ServerSocket listener = new ServerSocket(PORT);
        try {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }

    private static class Handler extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String ip = socket.getRemoteSocketAddress().toString().substring(1);
                System.out.println("New Connection From: " + ip);

                writers.add(out);

                while (true) {
                    String input = in.readLine();
                    if (input == null) return;
                    System.out.println(ip + ": " + input);
                    for (PrintWriter writer : writers)
                        writer.println(ip + ": " + input);
                    //writer.println("MESSAGE " + ip + ": " + input);
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            } finally {
                if (out != null) writers.remove(out);
                try {socket.close();} catch (Exception e) {}
            }
        }
    }
}
