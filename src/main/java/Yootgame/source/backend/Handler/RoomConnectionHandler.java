package Yootgame.source.backend.Handler;

import Yootgame.source.backend.multiroom.Room;
import Yootgame.source.backend.multiroom.RoomManager;

import java.io.*;
import java.net.*;

/**
 * RoomConnectionHandler 클래스는 각 클라이언트와의 연결을 관리하며,
 * 클라이언트 명령을 처리하고 적절한 응답을 전송합니다.
 * 또한, 클라이언트가 방을 생성하거나 참가할 수 있도록 지원합니다.
 */
public class RoomConnectionHandler extends Thread {
    private final Socket socket; // 클라이언트와의 연결을 관리하는 소켓
    private final RoomManager roomManager; // 방 관리 객체
    private Room currentRoom; // 클라이언트가 현재 참여 중인 방
    private PrintWriter out; // 클라이언트에게 메시지를 전송하는 스트림

    /**
     * 생성자 - RoomConnectionHandler 객체를 초기화합니다.
     *
     * @param socket      클라이언트 소켓
     * @param roomManager 방 관리 객체
     */
    public RoomConnectionHandler(Socket socket, RoomManager roomManager) {
        this.socket = socket;
        this.roomManager = roomManager;
    }

    /**
     * 클라이언트 연결을 처리하는 스레드의 실행 로직.
     * 클라이언트 명령을 수신하고 처리합니다.
     */
    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            this.out = out;
            out.println("The connection to the server was successful."); // 연결 성공 메시지 전송

            String command;
            // 클라이언트 명령 수신 및 처리
            while ((command = in.readLine()) != null) {
                processCommand(command);
            }
        } catch (IOException e) {
            e.printStackTrace(); // 예외 발생 시 스택 트레이스 출력
        } finally {
            leaveRoom(); // 현재 방에서 나가기
            try {
                socket.close(); // 소켓 닫기
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 클라이언트 명령을 처리하는 메서드.
     *
     * @param command 클라이언트가 전송한 명령 문자열
     */
    private void processCommand(String command) {
        if (command.startsWith("/create ")) {
            // 방 생성 명령 처리
            String[] params = command.substring(8).trim().split(" ");
            String roomName = params[0];
            int turnTime = params.length > 1 ? Integer.parseInt(params[1]) : 30;
            int maxPlayers = params.length > 2 ? Integer.parseInt(params[2]) : 4;
            roomManager.createRoom(roomName, turnTime, maxPlayers); // 방 생성
            out.println("Room '" + roomName + "' created.");
        } else if (command.startsWith("/join ")) {
            // 방 참가 명령 처리
            String roomName = command.substring(6).trim();
            if (currentRoom != null) {
                out.println("You are already in a room. Leave the current room first with /leave.");
                return;
            }
            Room room = roomManager.getRoom(roomName); // 방 조회
            if (room != null && room.addClient(this)) {
                currentRoom = room;
                out.println("Joined room '" + roomName + "'.");
            } else {
                out.println("Room does not exist or is full.");
            }
        } else if (command.equals("/list")) {
            // 방 목록 요청
            listRooms();
        } else if (command.equals("/leave")) {
            // 방 나가기 요청
            leaveRoom();
        } else if (command.equals("/quit")) {
            // 연결 종료 요청
            out.println("Terminate the connection to the server.");
            leaveRoom();
            interrupt();
        } else {
            // 알 수 없는 명령
            out.println("Unknown command.");
        }
    }

    /**
     * 서버에 생성된 방 목록을 클라이언트에게 전송합니다.
     */
    private void listRooms() {
        var roomList = roomManager.listRooms(); // 방 목록 가져오기
        if (roomList.isEmpty()) {
            out.println("Currently no rooms created.");
        } else {
            out.println("Currently created rooms:");
            for (Room room : roomList) {
                out.println("- " + room.getName() +
                        " (Turn Time: " + room.getTurnTime() + "s, Number of pieces: " + room.getNumberOfPiece() + ")");
            }
        }
    }

    /**
     * 현재 참여 중인 방에서 나갑니다.
     */
    private void leaveRoom() {
        if (currentRoom != null) {
            currentRoom.removeClient(this); // 방에서 클라이언트 제거
            out.println("Left room '" + currentRoom.getName() + "'.");
            if (currentRoom.isEmpty()) {
                roomManager.removeRoom(currentRoom.getName()); // 방이 비어 있으면 삭제
            }
            currentRoom = null; // 현재 방 정보 초기화
        } else {
            out.println("You are not in any room.");
        }
    }

    /**
     * 클라이언트에게 메시지를 전송합니다.
     *
     * @param message 전송할 메시지
     */
    public void sendMessage(String message) {
        if (out != null) {
            out.println(message); // 클라이언트로 메시지 전송
        }
    }
}
