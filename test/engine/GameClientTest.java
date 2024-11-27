package engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import entity.dto.BulletPositionDTO;
import entity.pvp.Responses;
import entity.dto.GameStateDTO;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
        String message = "CREATED-0000";
        gameClient.onMessage(message);

        assertTrue(responses.isRoomCreated());
        assertTrue(responses.isRoomOwner());
        assertEquals("0000", responses.getRoomCode());
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

        gameState.setP1X(1);
        gameState.setP1Y(2);
        gameState.setP2X(3);
        gameState.setP2Y(4);

        List<BulletPositionDTO> p1b = new ArrayList<>();
        BulletPositionDTO bp1 = new BulletPositionDTO();
        bp1.setBx(10);
        bp1.setBy(11);
        p1b.add(bp1);

        List<BulletPositionDTO> p2b = new ArrayList<>();
        BulletPositionDTO bp2 = new BulletPositionDTO();
        bp2.setBx(100);
        bp2.setBy(101);
        p2b.add(bp2);

        gameState.setP1b(p1b);
        gameState.setP2b(p2b);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(gameState);
        String message = "UPDATE-" + json;

        gameClient.onMessage(message);

        assertNotNull(responses.getGameStateDTO());

        assertEquals(gameState.getP1b().get(0).getBx(), responses.getGameStateDTO().getP1b().get(0).getBx());
        assertEquals(gameState.getP1b().get(0).getBy(), responses.getGameStateDTO().getP1b().get(0).getBy());
        assertEquals(gameState.getP1X(), responses.getGameStateDTO().getP1X());
        assertEquals(gameState.getP1Y(), responses.getGameStateDTO().getP1Y());

        assertEquals(gameState.getP2b().get(0).getBx(), responses.getGameStateDTO().getP2b().get(0).getBx());
        assertEquals(gameState.getP2b().get(0).getBy(), responses.getGameStateDTO().getP2b().get(0).getBy());
        assertEquals(gameState.getP2X(), responses.getGameStateDTO().getP2X());
        assertEquals(gameState.getP2Y(), responses.getGameStateDTO().getP2Y());
    }

    @Test
    void testOnMessage_GameOverWin() {
        String message = "GAMEOVER-WIN";
        gameClient.onMessage(message);

        assertTrue(responses.isGameOver());
        assertTrue(responses.isWin());
    }

    @Test
    void testOnMessage_GameOverLose() {
        String message = "GAMEOVER-LOSE";
        gameClient.onMessage(message);

        assertTrue(responses.isGameOver());
        assertFalse(responses.isWin());
    }

    @Test
    void testOnMessage_Error() {
        String message = "ERROR-reason";
        gameClient.onMessage(message);

        assertTrue(responses.isError());
    }


    @Test
    void testOnClose() {
        gameClient.onClose(0, "", true);

        assertTrue(responses.isError());
    }

    @Test
    void testOnError() {
        Exception ex = new Exception("Test exception");
        gameClient.onError(ex);

        assertTrue(responses.isError());
    }
}
