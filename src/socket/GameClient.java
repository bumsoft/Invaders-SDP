package socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import engine.Core;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import screen.PVP.GameStateDTO;
import screen.PVP.PositionResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public class GameClient extends WebSocketClient {

    private final Logger logger = Core.getLogger();
    private final Responses responses;
    private final ObjectMapper mapper = new ObjectMapper();

    public GameClient() throws URISyntaxException
    {
        super(new URI(Core.getServerWSUrl()));
        responses = new Responses();
    }

    @Override
    public void onOpen(ServerHandshake handshakedata)
    {
        logger.info("WebSocket connection opened");
    }

    @Override
    public void onMessage(String message)
    {
        logger.info("Received:"+message);
        String[] parts = message.split("-");
        parts[0] = parts[0].toUpperCase();

        switch(parts[0]){
            case "CREATED" ->{
                responses.setRoomCreated(true);
                responses.setRoomOwner(true);
                responses.setRoomCode(parts[1]); //룸코드
            }
            case "JOINED" ->{
                responses.setGameJoin(true);
                responses.setOpponent(parts[1]+"-"+parts[2]); //user1-user2
            }
            case "START" ->{
                responses.setGameStarted(true);
            }
            case "UPDATE" ->{
                try {
                    responses.setGameStateDTO(mapper.readValue(parts[1], GameStateDTO.class));

                }catch (IOException e){
                    logger.info("cannot translate json to PositionResponse:"+e.getMessage());
                }
            }
            case "GAMEOVER" ->{ //"GAMEOVER-WIN or -LOSE
                responses.setGameOver(true);
                if(parts[1].equals("WIN")) responses.setWin(true);
            }
            case "ERROR" ->{
                responses.setError(true);
            }
            default -> {
                logger.info("Unknown command:"+message);
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote)
    {
        logger.info("Connection closed: " + reason);
        responses.setError(true);
    }

    @Override
    public void onError(Exception ex) {
        logger.info(ex.getMessage());
        responses.setError(true);
    }

    public void createRoom(String username)
    {
        send("CREATE-"+username);
    }
    public void joinRoom(String username, String code)
    {
        send("JOIN-"+username+"-"+code);
    }
    public void readyRoom(String username)
    {
        send("READY-"+username);
    }

    public void sendOrder(String order, String username)
    {
        send(order+"-"+username);
    }

    public Responses getResponses()
    {
        return responses;
    }

}
