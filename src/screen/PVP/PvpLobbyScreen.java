package screen.PVP;


import engine.Core;
import engine.UserManager;
import screen.Screen;
import socket.GameClient;
import socket.Responses;

import java.awt.event.KeyEvent;


public class PvpLobbyScreen extends Screen {

    private final String username;

    /*
        for 0, create room
        for 1, join room
     */
    private int select = 0;
    private boolean isCodeWrite = false;
    private String code ="";
    private Responses responses;
    private GameClient gameClient;


    public PvpLobbyScreen(final int width, final int height, final int fps, final UserManager userManager)
    {
        super(width, height, fps);
        this.username = userManager.getUsername();
        try{
            this.gameClient = Core.getGameClient();
            this.responses = gameClient.getResponses();
        }catch (Exception e){
            e.printStackTrace();
            logger.info("ERROR");
            this.returnCode = 1;
            isRunning = false;
        }

    }

    @Override
    public void initialize() {
        super.initialize();
        this.returnCode = 1;
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

        if(responses.isGameJoin() || responses.isRoomCreated())
        {
            this.returnCode = 12; // WaitingRoomScreen
            isRunning = false;
        }

        if (inputManager.isKeyPressed(KeyEvent.VK_ENTER))
        {
            if(this.select == 0)
            {
                //방 생성 요청..
                gameClient.createRoom(username);
            }
            else if(this.select == 1 && !isCodeWrite)
            {
                //코드 입력란 뜨게..
                isCodeWrite = true;
            }
            else if(this.select == 1)
            {
                //방 참가요청..
                gameClient.joinRoom(username,code);
            }
        }
        if(inputManager.isKeyPressed(KeyEvent.VK_ESCAPE))
        {
            if(isCodeWrite)
            {
                isCodeWrite = false;
                code="";
            }
            else
            {
                this.returnCode = 1;
                this.isRunning = false;
            }
        }
        if(inputManager.isKeyPressed(KeyEvent.VK_W) || inputManager.isKeyPressed(KeyEvent.VK_UP))
        {
            if(!isCodeWrite) this.select = select==1?0:1;
        }
        if(inputManager.isKeyPressed(KeyEvent.VK_S) || inputManager.isKeyPressed(KeyEvent.VK_DOWN))
        {
            if(!isCodeWrite) this.select = select==1?0:1;
        }
        if(isCodeWrite)
        {
            if((inputManager.isKeyPressed(KeyEvent.VK_BACK_SPACE)))
            {
                if(!code.isEmpty()) code = code.substring(0,code.length()-1);
            }
            char typedChar = inputManager.getTypedKey();
            if (Character.isDigit(typedChar))
            {
                code += typedChar;
            }
        }

        draw();
    }


    private void draw() {
        drawManager.initDrawing(this);

        drawManager.drawPvpLobby(this,this.select,this.isCodeWrite,this.code);

        drawManager.completeDrawing(this);
    }
}

