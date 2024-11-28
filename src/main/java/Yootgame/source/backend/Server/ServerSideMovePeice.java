package Yootgame.source.backend.Server;

import Yootgame.source.backend.Handler.RoomConnectionHandler;
import Yootgame.source.backend.multiroom.Room;

/**
 * ServerSideMovePeice 클래스는 서버에서 말 이동 업데이트를 처리하고
 * 해당 정보를 방의 모든 클라이언트에게 전송하는 역할을 담당합니다.
 */
public class ServerSideMovePeice {
    private Room currentRoom; // 현재 동작 중인 방 객체

    /**
     * 생성자 - ServerSideMovePeice 객체를 초기화합니다.
     *
     * @param room 현재 서버가 처리 중인 Room 객체
     */
    public ServerSideMovePeice(Room room) {
        this.currentRoom = room;
    }

    /**
     * 말의 이동 정보를 방의 모든 클라이언트에게 전송
     *
     * @param player      이동을 수행한 플레이어 이름
     * @param piece       이동 중인 말
     * @param newPosition 말이 이동할 새로운 위치
     */
    public void sendMoveUpdate(String player, String piece, int newPosition) {
        // 이동 업데이트 메시지 생성
        String updateMessage = "MOVE_UPDATE " + player + " " + piece + " " + newPosition;

        // Room 객체에서 연결된 클라이언트 목록 가져오기
        for (RoomConnectionHandler client : currentRoom.getClients()) {
            // 각 클라이언트에게 메시지 전송
            client.sendMessage(updateMessage);
        }
    }

    /**
     * 게임 상태를 업데이트하고 클라이언트들에게 알림
     * @param player 이동을 수행한 플레이어 이름
     * @param piece  이동 중인 말
     * @param steps  이동할 칸 수 (단계)
     */
    public void updateAndNotify(String player, String piece, int steps) {
        // 서버의 게임 로직이 필요한 작업을 처리
        // 예: 데이터베이스 갱신, 내부 상태 변경 등

        // 말의 이동 업데이트를 클라이언트들에게 알림
        sendMoveUpdate(player, piece, steps);
    }
}
