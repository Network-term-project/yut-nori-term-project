package Yootgame.source.backend.Client;

import Yootgame.source.backend.multiroom.Room; // 다중 방 관련 클래스(Room) 사용

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Client 클래스는 서버와의 통신을 담당하며 사용자 입력을 처리하는 역할을 합니다.
 */
public class Client {
    // 서버 주소 및 포트 설정
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 12345;

    // 서버와 통신하기 위한 입출력 스트림
    private BufferedReader serverInput;
    private PrintWriter serverOutput;
    private Socket socket;

    // 클라이언트 상태 및 위치 추적 변수
    private boolean running = true;
    private Room currentRoom; // 사용자가 현재 있는 방
    private String currentLocation = "Lobby"; // 현재 위치 (기본값: Lobby)
    private String nickname; // 사용자 닉네임

    public static void main(String[] args) {
        Client client = new Client(); // 클라이언트 객체 생성
        client.start(); // 클라이언트 실행
    }

    /**
     * 클라이언트를 시작하고 서버와 연결을 설정합니다.
     */
    public void start() {
        try {
            // 서버에 연결
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            serverOutput = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Connected to server: " + SERVER_ADDRESS + ":" + SERVER_PORT);

            // 사용자 닉네임 설정
            setNickname();

            // 서버 메시지를 비동기로 수신하는 스레드 시작
            listenForUpdates();

            // 사용자 입력 처리
            handleUserInput();
        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 연결 종료
            closeConnection();
        }
    }

    /**
     * 사용자로부터 닉네임을 입력받아 서버에 전송합니다.
     * 닉네임 설정이 성공하면 사용자에게 확인 메시지를 출력합니다.
     */
    private void setNickname() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter your nickname: "); // 닉네임 입력 요청
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) { // 입력이 비어 있지 않을 경우 처리
                sendMessage("/nickname " + input); // 서버에 닉네임 전송
                try {
                    String response = serverInput.readLine(); // 서버 응답 읽기
                    if (response.startsWith("Nickname set:")) { // 닉네임 설정 성공 여부 확인
                        this.nickname = input; // 닉네임 저장
                        System.out.println(response); // 서버 응답 출력
                        break; // 닉네임 설정 완료 시 루프 종료
                    } else {
                        System.out.println(response); // 실패 메시지 출력
                    }
                } catch (IOException e) {
                    System.out.println("Error reading from server");
                }
            } else {
                System.out.println("Nickname cannot be empty. Please try again."); // 닉네임이 비었을 경우 메시지 출력
            }
        }
    }

    /**
     * 서버로 메시지를 전송합니다.
     *
     * @param message 전송할 메시지
     */
    private void sendMessage(String message) {
        serverOutput.println(message); // 서버로 메시지 출력
    }

    /**
     * 서버 메시지를 비동기로 수신하고 처리합니다.
     * 수신된 메시지에 따라 사용자의 위치를 업데이트합니다.
     */
    private void listenForUpdates() {
        new Thread(() -> {
            try {
                String message;
                while ((message = serverInput.readLine()) != null) {
                    // 서버 응답 처리
                    if (message.contains("Joined room")) { // 방 참가 시 위치 업데이트
                        String roomName = message.split("'")[1];
                        currentLocation = roomName;
                    } else if (message.contains("Left room")) { // 방 퇴장 시 로비로 이동
                        currentLocation = "Lobby";
                    }
                    System.out.println(message); // 서버 메시지 출력
                    System.out.print("[" + currentLocation + "] "); // 현재 위치 표시
                }
            } catch (IOException e) {
                if (running) {
                    System.out.println("Connection lost or error reading data: " + e.getMessage());
                }
            } finally {
                running = false; // 실행 상태 종료
                closeConnection(); // 연결 종료
            }
        }).start(); // 새로운 스레드에서 실행
    }

    /**
     * 사용자 입력을 처리하고 적절한 명령어를 서버로 전송합니다.
     */
    private void handleUserInput() {
        Scanner scanner = new Scanner(System.in);

        while (running) {
            try {
                System.out.print("[" + currentLocation + "] "); // 현재 위치 표시
                String userInput = scanner.nextLine(); // 사용자 입력 읽기

                if (userInput.equals("/quit")) { // 종료 명령어 처리
                    System.out.println("Exiting client...");
                    running = false;
                    sendMessage("/quit");
                    break;
                }

                if (userInput.startsWith("/")) { // 명령어 입력 시 처리
                    processCommand(userInput);
                } else {
                    sendMessage(userInput); // 일반 메시지 전송
                }

            } catch (Exception e) {
                System.out.println("Error processing input: " + e.getMessage());
            }
        }
        scanner.close(); // 스캐너 종료
    }

    /**
     * 사용자가 입력한 명령어를 처리하고 서버로 전송합니다.
     *
     * @param command 사용자 명령어
     */
    private void processCommand(String command) {
        if (command.startsWith("/create ")) { // 방 생성 명령어 처리
            sendMessage(command);
            String roomName = command.split(" ")[1];
            currentLocation = roomName; // 방 생성 시 위치 업데이트
        } else if (command.startsWith("/join ")) { // 방 참가 명령어 처리
            sendMessage(command);
            String roomName = command.split(" ")[1];
            currentLocation = roomName; // 방 참가 시 위치 업데이트
        } else if (command.startsWith("/leave")) { // 방 퇴장 명령어 처리
            sendMessage(command);
            currentLocation = "Lobby"; // 로비로 위치 변경
        } else if (command.startsWith("/list") || command.equals("/quit")) { // 리스트 조회 및 종료 명령어 처리
            sendMessage(command);
        } else { // 알 수 없는 명령어 처리
            System.out.println("Unknown command. Available commands:");
            System.out.println("/create [roomName] [turnTime] [maxPlayers]");
            System.out.println("/join [roomName]");
            System.out.println("/list");
            System.out.println("/leave");
            System.out.println("/quit");
        }
    }

    /**
     * 서버와의 연결을 종료합니다.
     */
    private void closeConnection() {
        try {
            if (serverInput != null) serverInput.close(); // 입력 스트림 종료
            if (serverOutput != null) serverOutput.close(); // 출력 스트림 종료
            if (socket != null && !socket.isClosed()) socket.close(); // 소켓 종료
            System.out.println("Connection to server closed.");
        } catch (IOException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}
