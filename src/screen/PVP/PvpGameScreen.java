package screen.PVP;

import engine.Core;
import engine.UserManager;
import screen.Screen;
import socket.GameClient;
import socket.Responses;

import java.awt.event.KeyEvent;

public class PvpGameScreen extends Screen {

    private final String username;
    private GameClient gameClient;

    private Responses responses;



    public PvpGameScreen(final int width, final int height, final int fps, UserManager userManager) {
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
        this.returnCode = 14; //게임결과화면으로.
    }

    @Override
    public int run()
    {
        super.run();
        return returnCode;
    }

    @Override
    protected void update() {
        if (inputManager.isKeyPressed(KeyEvent.VK_SPACE))
        {
            gameClient.sendOrder("SHOOT",username);
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
