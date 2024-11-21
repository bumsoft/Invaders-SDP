package screen.PVP;

import engine.Core;
import engine.UserManager;
import entity.Ship;
import screen.Screen;
import socket.GameClient;
import socket.PvpShip;
import socket.Responses;

import java.awt.event.KeyEvent;

public class PvpGameScreen extends Screen {

    private final String username;
    private GameClient gameClient;

    private Responses responses;

    private PvpShip myShip = new PvpShip(0,0);
    private PvpShip opShip = new PvpShip(0,0);

    private PositionResponse positionResponse;

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
        if(inputManager.isKeyPressed(KeyEvent.VK_A) || inputManager.isKeyPressed(KeyEvent.VK_LEFT))
        {
            gameClient.sendOrder("LEFT",username);
        }
        if(inputManager.isKeyPressed(KeyEvent.VK_D) || inputManager.isKeyPressed(KeyEvent.VK_RIGHT))
        {
            gameClient.sendOrder("RIGHT",username);
        }

        if(responses.getPositionResponse()!=null && positionResponse!=null
            && positionResponse != responses.getPositionResponse())
        {
            positionResponse = responses.getPositionResponse();
            myShip.setPositionX(positionResponse.getPlayerX());
            myShip.setPositionY(positionResponse.getPlayerY());
            opShip.setPositionX(positionResponse.getEnemyPlayerX());
            opShip.setPositionY(positionResponse.getEnemyPlayerY());
        }

        draw();
    }


    private void draw() {
        drawManager.initDrawing(this);
        drawManager.drawCenteredText(this, "PVP Game Screen", 100);

        drawManager.drawEntity(myShip, myShip.getPositionX(), myShip.getPositionY());
        drawManager.drawEntity(opShip, opShip.getPositionX(), opShip.getPositionY());

        drawManager.completeDrawing(this);
    }
}
