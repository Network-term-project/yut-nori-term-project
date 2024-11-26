package Yootgame.source.backend.Server;

import Yootgame.source.backend.Handler.YutGameSessionHandler;
import Yootgame.source.backend.multiroom.RoomManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Server {
    private static final int PORT = 12345;
    private static Map<String, Integer> gameState = new HashMap<>();
    private static List<PrintWriter> clientWriters = new ArrayList<>();
    private static RoomManager roomManager = new RoomManager();

    public static void main(String[] args) {
        System.out.println("Unified Game Server started on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected - IP: " + clientSocket.getInetAddress() +
                        ", Port: " + clientSocket.getPort());
                new YutGameSessionHandler(clientSocket, roomManager, gameState, clientWriters).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}