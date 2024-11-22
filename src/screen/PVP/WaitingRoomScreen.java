package screen.PVP;

import engine.Core;
import engine.UserManager;
import screen.Screen;
import socket.GameClient;
import socket.Responses;

import java.awt.event.KeyEvent;


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
        if(responses.isError())
        {
            this.isRunning=false;
            this.returnCode = 1;
            Core.removeGameClient();
            logger.info("Connection closed. Return to Title Screen");
            return;
        }
        if (inputManager.isKeyPressed(KeyEvent.VK_ENTER) && responses.isGameJoin()) //상대가 있는 경우만 READY가능
        {
            gameClient.readyRoom(username);
            isReady = true;
        }
        if(inputManager.isKeyPressed(KeyEvent.VK_ESCAPE)) //esc누르면 메뉴로
        {
            Core.removeGameClient();
            this.isRunning = false;
            this.returnCode = 1;
        }
        if(responses.isGameStarted())
        {
            isRunning = false;
        }
        draw();
    }


    private void draw() {
        drawManager.initDrawing(this);
        drawManager.drawCenteredText(this, "Waiting Game Start", 100);

        if (isReady)
            drawManager.drawCenteredText(this, "READY!", 130);
        else
            drawManager.drawCenteredText(this, username +"PRESS ENTER TO READY", 130);

        if(responses.isGameJoin())
            drawManager.drawCenteredText(this, "Opponent: "+responses.getOpponent(), 160);
        else
            drawManager.drawCenteredText(this, "Waiting Opponent", 160);

        if(responses.isRoomOwner())
            drawManager.drawCenteredBigString(this, "Access code: "+responses.getRoomCode(), 300);

        drawManager.completeDrawing(this);
    }
}
