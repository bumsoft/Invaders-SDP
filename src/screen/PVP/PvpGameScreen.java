package screen.PVP;

import engine.Cooldown;
import engine.Core;
import engine.DrawManager;
import engine.UserManager;
import screen.Screen;
import socket.GameClient;
import socket.PvpShip;
import socket.Responses;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class PvpGameScreen extends Screen {
    private static final int SHOT_INTERVAL = 500;
    private static final int MOVE_INTERVAL = 100;
    private final String username;
    private GameClient gameClient;

    private Responses responses;

    private PvpShip myShip;
    private PvpShip opShip;

    private List<myBullet> myBullets = new ArrayList<>();
    private List<enBullet> enBullets = new ArrayList<>();

    private GameStateDTO gameStateDTO;

    /** cooldown for shot interval*/
    private Cooldown shotCooldown;

    /** cooldown for moving interval*/
    private Cooldown moveCooldown;

    public PvpGameScreen(final int width, final int height, final int fps, UserManager userManager) {
        super(width, height, fps);
        moveCooldown = Core.getCooldown(MOVE_INTERVAL);
        shotCooldown = Core.getCooldown(SHOT_INTERVAL);
        moveCooldown.reset();
        shotCooldown.reset();
        this.username = userManager.getUsername();
        try{
            this.gameClient = Core.getGameClient();
            responses = gameClient.getResponses();
            if(responses.isRoomOwner())
            {
                myShip = new PvpShip(0,0, DrawManager.SpriteType.Ship, Color.blue);
                opShip = new PvpShip(0,0, DrawManager.SpriteType.PvpEnemy, Color.red);
            }
            else
            {
                myShip = new PvpShip(0,0, DrawManager.SpriteType.PvpEnemy, Color.blue);
                opShip = new PvpShip(0,0, DrawManager.SpriteType.Ship, Color.red);
            }


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
        if (shotCooldown.checkFinished() && inputManager.isKeyDown(KeyEvent.VK_SPACE))
        {
            shotCooldown.reset();
            gameClient.sendOrder("SHOOT",username);
        }
        if(moveCooldown.checkFinished() && inputManager.isKeyDown(KeyEvent.VK_A) || inputManager.isKeyDown(KeyEvent.VK_LEFT))
        {
            moveCooldown.reset();
            gameClient.sendOrder("LEFT",username);
        }
        if(moveCooldown.checkFinished() && inputManager.isKeyDown(KeyEvent.VK_D) || inputManager.isKeyDown(KeyEvent.VK_RIGHT))
        {
            moveCooldown.reset();
            gameClient.sendOrder("RIGHT",username);
        }

        if(responses.getGameStateDTO() != null
            && gameStateDTO != responses.getGameStateDTO())
        {
            gameStateDTO = responses.getGameStateDTO();
            myShip.setPositionX(gameStateDTO.getP1X());
            myShip.setPositionY(gameStateDTO.getP1Y());
            opShip.setPositionX(gameStateDTO.getP2X());
            opShip.setPositionY(gameStateDTO.getP2Y());
            myBullets = new ArrayList<>();
            enBullets = new ArrayList<>();

            for(BulletPositionDTO bulletPositionDTO : gameStateDTO.getP1b())
            {
                myBullets.add(new myBullet(bulletPositionDTO.getBx(), bulletPositionDTO.getBy()));
            }
            for(BulletPositionDTO bulletPositionDTO : gameStateDTO.getP2b())
            {
                enBullets.add(new enBullet(bulletPositionDTO.getBx(), bulletPositionDTO.getBy()));
            }
        }

        draw();
    }


    private void draw() {
        drawManager.initDrawing(this);

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
