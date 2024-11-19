package screen;

import engine.UserManager;
import entity.dto.UserScoreDto;

import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Register screen class.
 */
public class RankScreen extends Screen {
    private final List<UserScoreDto> scoreList;

    public RankScreen(final int width, final int height, final int fps, final UserManager userManager) {
        super(width, height, fps);
        this.scoreList = userManager.getRanking();
    }

    @Override
    public void initialize() {
        super.initialize();
        this.returnCode = 1; // Default return code
    }

    @Override
    public int run()
    {
        super.run();
        return returnCode;
    }

    @Override
    protected void update() {
        if (inputManager.isKeyPressed(KeyEvent.VK_ESCAPE)) {
            isRunning = false;

        // Draw screen
        draw();
    }



    private void draw() {
        drawManager.initDrawing(this);
        drawManager.drawCenteredText(this, "Rank", 100);
        for(int i=1;i<=10;i++)
        {
            user
        }
        }
        drawManager.completeDrawing(this);
    }
}
