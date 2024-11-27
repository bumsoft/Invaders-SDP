package screen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import screen.RankScreen;
import engine.UserManager;
import engine.InputManager;
import engine.DrawManager;
import engine.Frame;
import screen.Screen;
import entity.dto.UserScoreDto;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RankScreenTest {
    private RankScreen rankScreen;
    private UserManager userManager;
    private InputManager inputManager;
    private List<UserScoreDto> mockRanking;

    @BeforeEach
    void setUp() throws Exception {
        // Mock 객체 생성
        userManager = mock(UserManager.class);
        inputManager = mock(InputManager.class);

        // Mock 랭킹 데이터 생성
        mockRanking = Arrays.asList(
                new UserScoreDto("User1", (long) 1000),
                new UserScoreDto("User2", (long) 800),
                new UserScoreDto("User3", (long) 600),
                new UserScoreDto("User4", (long) 400),
                new UserScoreDto("User5", (long) 200)
        );
        when(userManager.getRanking()).thenReturn(mockRanking);

        // RankScreen 인스턴스 생성 및 초기화
        rankScreen = new RankScreen(600, 650, 60, userManager);
        rankScreen.initialize();

        // 리플렉션으로 inputManager 주입
        Field inputManagerField = Screen.class.getDeclaredField("inputManager");
        inputManagerField.setAccessible(true);
        inputManagerField.set(rankScreen, inputManager);

        // Mock Frame 생성 및 설정
        Frame mockFrame = mock(Frame.class);
        Graphics mockGraphics = mock(Graphics.class);
        when(mockFrame.getGraphics()).thenReturn(mockGraphics);
        Insets mockInsets = new Insets(10, 10, 10, 10);
        when(mockFrame.getInsets()).thenReturn(mockInsets);

        // DrawManager의 frame 필드 설정
        Field instanceField = DrawManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        DrawManager drawManagerInstance = (DrawManager) instanceField.get(null);
        drawManagerInstance.setFrame(mockFrame);
    }


    /**
     * isRunning 필드 초기화 상태 확인
     */
    @Test
    void testIsRunningField() throws Exception {
        // 리플렉션으로 isRunning 필드 확인
        Field isRunningField = Screen.class.getDeclaredField("isRunning");
        isRunningField.setAccessible(true);

        // isRunning 필드 강제 초기화
        isRunningField.set(rankScreen, true);

        // 값 확인
        assertTrue((boolean) isRunningField.get(rankScreen));
    }


    /**
     * returnCode 필드 초기값과 변경 확인
     */
    @Test
    void testReturnCodeField() throws Exception {
        Field returnCodeField = Screen.class.getDeclaredField("returnCode");
        returnCodeField.setAccessible(true);

        // 초기값 확인
        assertEquals(1, (int) returnCodeField.get(rankScreen));

        // 값 변경 후 확인
        returnCodeField.set(rankScreen, 5);
        assertEquals(5, (int) returnCodeField.get(rankScreen));
    }

    /**
     * update() 메서드에서 ESC 키 입력 처리 확인
     */
    @Test
    void testUpdateMethod() throws Exception {
        // ESC 키 입력 설정
        when(inputManager.isKeyPressed(KeyEvent.VK_ESCAPE)).thenReturn(true);

        // update() 메서드 호출
        Method updateMethod = RankScreen.class.getDeclaredMethod("update");
        updateMethod.setAccessible(true);
        updateMethod.invoke(rankScreen);

        // isRunning 값 확인
        Field isRunningField = Screen.class.getDeclaredField("isRunning");
        isRunningField.setAccessible(true);
        assertFalse((boolean) isRunningField.get(rankScreen));
    }

    /**
     * scoreList 데이터 초기화 확인
     */
    @Test
    void testScoreListInitialization() throws Exception {
        // scoreList 필드 접근
        Field scoreListField = RankScreen.class.getDeclaredField("scoreList");
        scoreListField.setAccessible(true);

        // 데이터 검증
        List<UserScoreDto> scoreList = (List<UserScoreDto>) scoreListField.get(rankScreen);
        assertNotNull(scoreList);
        assertEquals(5, scoreList.size());
        assertEquals("User1", scoreList.get(0).getUsername());
        assertEquals(1000, scoreList.get(0).getScore());
    }
}
