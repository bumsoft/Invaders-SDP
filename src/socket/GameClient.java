package socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import engine.Core;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

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
        message = message.toUpperCase();
        logger.info("Received:"+message);
        String[] parts = message.split("-");

        switch(parts[0]){
            case "CREATED" ->{
                responses.setRoomCreated(true);
                responses.setRoomOwner(true);
                responses.setRoomCode(parts[1]); //룸코드
            }
            case "JOINED" ->{
                responses.setGameJoin(true);
                responses.setOpponent(parts[1]); //상대이름
            }
            case "START" ->{
                responses.setGameStarted(true);
            }
            case "UPDATE" ->{
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
    }

    @Override
    public void onError(Exception ex) {
        logger.info(ex.getMessage());
        ex.printStackTrace();
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
