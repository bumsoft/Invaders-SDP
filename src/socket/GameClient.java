package socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import engine.Core;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.logging.Logger;

public class GameClient extends WebSocketClient {

    private final Logger logger = Core.getLogger();
    private final Responses responses = new Responses();
    //private final ObjectMapper mapper = new ObjectMapper(); 나중에 json용

    public GameClient() throws URISyntaxException
    {
        super(new URI(Core.getServerWSUrl()));
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

        if(message.startsWith("CREATED-"))//방장이 받는 메시지
        {
            responses.setRoomCreated(true);
            responses.setRoomCode(message.substring(8)); //룸코드
        }
        if(message.startsWith("JOINED-"))
        {
            responses.setGameJoin(true);
            responses.setOpponent(message.substring(7)); //상대이름
        }
        if(message.startsWith("START"))
        {
            responses.setGameStarted(true);
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

    public void sendOrder(String order)
    {

    }

    public Responses getResponses()
    {
        return responses;
    }

    public static void main(String[] args) {
        try {

            GameClient client = new GameClient();
            client.connectBlocking();

            // 사용자 입력을 통해 명령 전송
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("Enter order (LEFT, RIGHT, UP, DOWN): ");
                String order = scanner.nextLine().toUpperCase();

                if ("EXIT".equals(order)) {
                    break;
                }

                client.sendOrder(order);
            }
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
