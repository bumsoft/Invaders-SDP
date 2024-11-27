package engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import entity.pvp.Responses;
import entity.dto.GameStateDTO;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameClientTest {

    private GameClient gameClient;
    private Responses responses;

    @BeforeEach
    void setUp() throws URISyntaxException {
        gameClient = Mockito.spy(new GameClient());
        responses = gameClient.getResponses();
    }

    @Test
    void testOnOpen() {
        ServerHandshake mockHandshake = mock(ServerHandshake.class);
        gameClient.onOpen(mockHandshake);

        assertFalse(responses.isError()); // 에러가 없음을 확인
    }

    @Test
    void testOnMessage_Created() {
        String message = "CREATED-room123";
        gameClient.onMessage(message);

        assertTrue(responses.isRoomCreated());
        assertTrue(responses.isRoomOwner());
        assertEquals("room123", responses.getRoomCode());
    }

    @Test
    void testOnMessage_Joined() {
        String message = "JOINED-user1-user2";
        gameClient.onMessage(message);

        assertTrue(responses.isGameJoin());
        assertEquals("user1-user2", responses.getOpponent());
    }

    @Test
    void testOnMessage_Start() {
        String message = "START";
        gameClient.onMessage(message);

        assertTrue(responses.isGameStarted());
    }

    @Test
    void testOnMessage_Update() throws Exception {
        GameStateDTO gameState = new GameStateDTO();
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(gameState);
        String message = "UPDATE-" + json;

        gameClient.onMessage(message);

        assertNotNull(responses.getGameStateDTO());
        assertEquals(gameState, responses.getGameStateDTO());
    }

    @Test
    void testOnMessage_GameOverWin() {
        String message = "GAMEOVER-WIN";
        gameClient.onMessage(message);

        assertTrue(responses.isGameOver());
        assertTrue(responses.isWin());
    }

    @Test
    void testOnMessage_Error() {
        String message = "ERROR";
        gameClient.onMessage(message);

        assertTrue(responses.isError());
    }

    @Test
    void testOnMessage_UnknownCommand() {
        String message = "UNKNOWN-command";
        gameClient.onMessage(message);

        assertFalse(responses.isError()); // 에러 플래그가 트리거되지 않음
    }

    @Test
    void testOnClose() {
        gameClient.onClose(1000, "Normal Closure", true);

        assertTrue(responses.isError());
    }

    @Test
    void testOnError() {
        Exception ex = new Exception("Test exception");
        gameClient.onError(ex);

        assertTrue(responses.isError());
    }

    @Test
    void testCreateRoom() {
        gameClient.createRoom("user123");
        verify(gameClient).send("CREATE-user123");
    }

    @Test
    void testJoinRoom() {
        gameClient.joinRoom("user123", "room123");
        verify(gameClient).send("JOIN-user123-room123");
    }

    @Test
    void testReadyRoom() {
        gameClient.readyRoom("user123");
        verify(gameClient).send("READY-user123");
    }

    @Test
    void testSendOrder() {
        gameClient.sendOrder("MOVE", "user123");
        verify(gameClient).send("MOVE-user123");
    }
}
