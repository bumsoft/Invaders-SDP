package screen.PVP;


import engine.Core;
import engine.UserManager;

import screen.Screen;
import socket.GameClient;
import socket.Responses;

import java.awt.event.KeyEvent;


public class GameOverScreen extends Screen {

    private GameClient gameClient;

    private Responses responses;


    public GameOverScreen(final int width, final int height, final int fps, UserManager userManager) {
        super(width, height, fps);

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
        this.returnCode = 1; //게임결과화면으로.
    }

    @Override
    public int run()
    {
        super.run();
        return returnCode;
    }

    @Override
    protected void update() {


        if (inputManager.isKeyPressed(KeyEvent.VK_ESCAPE))
        {
            Core.removeGameClient();
            this.isRunning = false;
        }

        draw();
    }


    private void draw() {
        drawManager.initDrawing(this);

        if(responses.isWin())
            drawManager.drawCenteredBigString(this,"YOU WIN!",300);
        else
            drawManager.drawCenteredBigString(this,"YOU LOST!",300);

        drawManager.completeDrawing(this);
    }
}

