package screen.PVP;

import engine.Core;
import engine.UserManager;
import screen.Screen;
import socket.GameClient;
import socket.Responses;

import java.awt.event.KeyEvent;

/**
 * Login screen class.
 */
public class WaitingRoomScreen extends Screen {

    private final String username;
    private GameClient gameClient;
    private boolean isReady = false;
    private Responses responses;

    public WaitingRoomScreen(final int width, final int height, final int fps, UserManager userManager) {
        super(width, height, fps);
        this.username = userManager.getUsername();

        try{
            this.gameClient = Core.getGameClient();
            responses = gameClient.getResponses();
        }catch(Exception e){
            logger.info(e.getStackTrace().toString());
            this.returnCode = 1;
            isRunning = false;
        }

    }

    @Override
    public void initialize() {
        super.initialize();
        this.returnCode = 13; // 게임화면으로 바꾸기
    }

    @Override
    public int run()
    {
        super.run();
        return returnCode;
    }

    @Override
    protected void update() {
        if (inputManager.isKeyPressed(KeyEvent.VK_ENTER))
        {
            gameClient.readyRoom(username);
            isReady = true;
        }


        draw();
    }


    private void draw() {
        drawManager.initDrawing(this);
        drawManager.drawCenteredText(this, "Waiting Game Start", 100);
        if (isReady)
            drawManager.drawCenteredText(this, username +"is ready!", 130);
        else
            drawManager.drawCenteredText(this, username +"is not ready!", 130);

        if(responses.isGameJoin())
            drawManager.drawCenteredText(this, responses.getOpponent()+" is not ready!", 160);

        drawManager.drawCenteredBigString(this, "Access code: "+responses.getRoomCode(), 300);

        drawManager.completeDrawing(this);
    }
}
