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
        if (inputManager.isKeyPressed(KeyEvent.VK_ESCAPE))
            isRunning = false;

        // Draw screen
        draw();
    }



    private void draw() {
        drawManager.initDrawing(this);

        // 1. 화면 제목 출력
        drawManager.drawTitle(this, "RANKING");

        // 2. 헤더 출력 (RANK, ID, SCORE)
        int rankHeaderX = 130;  // RANK 텍스트 X 좌표
        int idHeaderX = 280;    // ID 텍스트 X 좌표
        int scoreHeaderX = 420; // SCORE 텍스트 X 좌표
        int headerY = 200;      // 헤더 Y 좌표

        drawManager.drawRegularString(this, "RANK", rankHeaderX, headerY);
        drawManager.drawRegularString(this, "ID", idHeaderX, headerY);
        drawManager.drawRegularString(this, "SCORE", scoreHeaderX, headerY);

        // 3. 헤더 하단 구분선
        drawManager.drawRegularString(this, "--------------------------------------", rankHeaderX, headerY + 30);

        // 4. 랭킹 데이터 출력 (상위 5명만)
        int startY = 260; // 첫 번째 데이터 출력 위치
        int lineHeight = 40; // 각 줄 간격

        for (int i = 0; i < scoreList.size() && i < 5; i++) {
            UserScoreDto score = scoreList.get(i);

            // 데이터 출력: Rank, ID, Score
            String rank = String.format("%d", i + 1); // Rank
            String id = score.getUsername();         // ID
            String points = String.format("%d", score.getScore()); // Score

            drawManager.drawRegularString(this, rank, rankHeaderX, startY + (i * lineHeight));
            drawManager.drawRegularString(this, id, idHeaderX, startY + (i * lineHeight));
            drawManager.drawRegularString(this, points, scoreHeaderX, startY + (i * lineHeight));
        }
        drawManager.completeDrawing(this);
    }
}
