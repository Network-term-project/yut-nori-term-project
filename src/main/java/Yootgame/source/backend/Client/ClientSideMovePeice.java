package Yootgame.source.backend.Client;

/**
 * ClientSideMovePeice 클래스는 클라이언트에서 말의 이동 업데이트 메시지를 처리하는 역할을 합니다.
 */
public class ClientSideMovePeice {

    /**
     * 이동 업데이트 메시지를 처리하는 메서드.
     * 서버로부터 전달받은 이동 업데이트 메시지를 파싱하여 사용자에게 이동 정보를 출력합니다.
     *
     * @param message 서버로부터 전달된 이동 업데이트 메시지 (형식: "MOVE_UPDATE [player] [piece] [newPosition]")
     */
    public void processMoveUpdate(String message) {
        // 메시지를 공백 기준으로 분리하여 배열에 저장
        String[] parts = message.split(" ");

        // 메시지 형식이 올바른지 확인 (4개의 요소가 있고 첫 번째 요소는 "MOVE_UPDATE"이어야 함)
        if (parts.length == 4 && parts[0].equals("MOVE_UPDATE")) {
            // 플레이어 이름, 이동할 말, 새로운 위치를 파싱
            String player = parts[1]; // 이동을 수행한 플레이어
            String piece = parts[2];  // 이동 중인 말
            int newPosition = Integer.parseInt(parts[3]); // 새로운 위치 (정수로 변환)

            // 이동 정보를 사용자에게 출력
            System.out.println("플레이어: " + player + ", 말: " + piece + ", 새 위치: " + newPosition);
        } else {
            // 메시지 형식이 잘못되었을 경우 오류 메시지 출력
            System.out.println("잘못된 MOVE_UPDATE 메시지 형식: " + message);
        }
    }
}
