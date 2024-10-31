package socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Scanner;

public class GameClient extends WebSocketClient {

    private final ObjectMapper mapper = new ObjectMapper();

    public GameClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to the server");
    }

    @Override
    public void onMessage(String message) {
        try {
            // 서버로부터 받은 응답을 GameResponseDTO로 변환
            GameResponseDTO response = mapper.readValue(message, GameResponseDTO.class);
            System.out.println("Received coordinates from server: x = " + response.getX() + ", y = " + response.getY());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public void sendOrder(String order) {
        try {
            // GameRequestDTO를 생성하여 JSON 형식으로 전송
            GameRequestDTO request = new GameRequestDTO(order);
            String message = mapper.writeValueAsString(request);
            send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            URI uri = new URI("ws://localhost:8080/game");
            GameClient client = new GameClient(uri);
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
