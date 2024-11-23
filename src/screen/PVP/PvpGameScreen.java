package screen.PVP;

import engine.Core;
import engine.UserManager;
import entity.Bullet;
import entity.Ship;
import screen.Screen;
import socket.GameClient;
import socket.PvpShip;
import socket.Responses;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class PvpGameScreen extends Screen {

    private final String username;
    private GameClient gameClient;

    private Responses responses;

    private PvpShip myShip;
    private PvpShip opShip;

    private List<myBullet> myBullets = new ArrayList<>();
    private List<enBullet> enBullets = new ArrayList<>();

    private GameStateDTO gameStateDTO;

    public PvpGameScreen(final int width, final int height, final int fps, UserManager userManager) {
        super(width, height, fps);
        this.username = userManager.getUsername();

        try{
            this.gameClient = Core.getGameClient();
            responses = gameClient.getResponses();
            myShip = new PvpShip(0,0, responses.isRoomOwner());
            opShip = new PvpShip(0,0, responses.isRoomOwner());
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
        if(responses.isError())
        {
            this.isRunning=false;
            this.returnCode = 1;
            Core.removeGameClient();
            logger.info("Connection closed. Return to Title Screen");
            return;
        }
        if(responses.isGameOver())
        {
            this.isRunning=false;
            this.returnCode = 14;
            return;
        }
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

        if(responses.getGameStateDTO() != null
            && gameStateDTO != responses.getGameStateDTO())
        {
            gameStateDTO = responses.getGameStateDTO();
            myShip.setPositionX(gameStateDTO.getPlayer1X());
            myShip.setPositionY(gameStateDTO.getPlayer1Y());
            opShip.setPositionX(gameStateDTO.getPlayer2X());
            opShip.setPositionY(gameStateDTO.getPlayer2Y());
            myBullets = new ArrayList<>();
            enBullets = new ArrayList<>();

            for(BulletPositionDTO bulletPositionDTO : gameStateDTO.getPlayer1BulletPositionDTO())
            {
                myBullets.add(new myBullet(bulletPositionDTO.getBulletX(), bulletPositionDTO.getBulletY()));
            }
            for(BulletPositionDTO bulletPositionDTO : gameStateDTO.getPlayer2BulletPositionDTO())
            {
                enBullets.add(new enBullet(bulletPositionDTO.getBulletX(), bulletPositionDTO.getBulletY()));
            }
        }

        draw();
    }


    private void draw() {
        drawManager.initDrawing(this);
        drawManager.drawCenteredText(this, "PVP Game Screen", 100);

        drawManager.drawEntity(myShip, myShip.getPositionX(), myShip.getPositionY());
        drawManager.drawEntity(opShip, opShip.getPositionX(), opShip.getPositionY());

        for(myBullet myBullet : myBullets)
        {
            drawManager.drawEntity(myBullet, myBullet.getPositionX(), myBullet.getPositionY());
        }
        for(enBullet enBullet : enBullets)
        {
            drawManager.drawEntity(enBullet, enBullet.getPositionX(), enBullet.getPositionY());
        }

        drawManager.completeDrawing(this);
    }
}
