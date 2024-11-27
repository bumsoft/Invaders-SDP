package screen.pvp;

import engine.Core;
import engine.UserManager;
import screen.Screen;
import engine.GameClient;
import entity.pvp.Responses;

import java.awt.event.KeyEvent;


public class WaitingRoomScreen extends Screen {

    private final String username;
    private GameClient gameClient;
    private boolean isReady = false;
    private Responses responses;
    private String opponent;

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
        if(responses.isGameJoin())
        {
            String users = responses.getOpponent();
            String[] parts = users.split("-");
            opponent = parts[0].equals(username) ? parts[1] : parts[0];
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
        drawManager.drawCenteredText(this, "Waiting Room", this.height/6);

        if (isReady)
        {
            drawManager.drawCenteredBigString(this, "READY!", this.height / 6 * 2);
            drawManager.drawCenteredRegularString(this, "Waiting " + opponent + " to get ready", this.height / 6 * 2 + 30);
        }
        else if(responses.isGameJoin())
            drawManager.drawCenteredRegularString(this, "PRESS ENTER TO READY", this.height/6 * 2);

        if(responses.isGameJoin())
            drawManager.drawCenteredBigString(this, "Opponent: "+opponent, this.height/6 * 3);
        else
            drawManager.drawCenteredBigString(this, "Waiting Opponent", this.height/6 * 3);

        if(responses.isRoomOwner())
            drawManager.drawCenteredBigString(this, "Access code: "+responses.getRoomCode(), this.height/6 * 5);

        drawManager.completeDrawing(this);
    }
}
