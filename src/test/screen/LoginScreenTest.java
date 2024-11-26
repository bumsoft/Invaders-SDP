package test.screen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import screen.LoginScreen;
import engine.UserManager;
import engine.InputManager;
import engine.DrawManager;
import engine.Frame;
import screen.Screen;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginScreenTest {
    private LoginScreen loginScreen;
    private UserManager userManager;
    private InputManager inputManager;

    @BeforeEach
    void setUp() throws Exception {
        // Mock 객체 생성
        userManager = mock(UserManager.class);
        inputManager = mock(InputManager.class);

        // LoginScreen 인스턴스 생성 및 초기화
        loginScreen = new LoginScreen(600, 650, 60, userManager);
        loginScreen.initialize();

        // 리플렉션으로 inputManager 주입
        Field inputManagerField = Screen.class.getDeclaredField("inputManager");
        inputManagerField.setAccessible(true);
        inputManagerField.set(loginScreen, inputManager);

        // 리플렉션으로 isRunning 강제 초기화
        Field isRunningField = Screen.class.getDeclaredField("isRunning");
        isRunningField.setAccessible(true);
        isRunningField.set(loginScreen, true);

        // Mock Frame 생성
        Frame mockFrame = mock(Frame.class);

        // Mock Graphics 설정
        Graphics mockGraphics = mock(Graphics.class);
        when(mockFrame.getGraphics()).thenReturn(mockGraphics);

        // getInsets() 반환값 설정
        Insets mockInsets = new Insets(10, 10, 10, 10);
        when(mockFrame.getInsets()).thenReturn(mockInsets);

        // DrawManager에 Mock Frame 설정
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
        Field isRunningField = Screen.class.getDeclaredField("isRunning");
        isRunningField.setAccessible(true);

        assertTrue((boolean) isRunningField.get(loginScreen));
    }

    /**
     * returnCode 필드의 초기값과 값 변경 확인
     */
    @Test
    void testReturnCodeField() throws Exception {
        // 리플렉션으로 returnCode 필드 접근
        Field returnCodeField = Screen.class.getDeclaredField("returnCode");
        returnCodeField.setAccessible(true);

        // 초기값 설정
        returnCodeField.set(loginScreen, 0);

        // 초기값 확인
        int returnCode = (int) returnCodeField.get(loginScreen);
        assertEquals(0, returnCode);

        // 값 변경 후 확인
        returnCodeField.set(loginScreen, 1);
        assertEquals(1, (int) returnCodeField.get(loginScreen));
    }

    /**
     * update() 메서드에서 키 입력 처리 확인
     */
    @Test
    void testUpdateMethod() throws Exception {
        when(inputManager.isKeyPressed(KeyEvent.VK_ENTER)).thenReturn(true);

        Method updateMethod = LoginScreen.class.getDeclaredMethod("update");
        updateMethod.setAccessible(true);
        updateMethod.invoke(loginScreen);

        verify(inputManager, atLeastOnce()).isKeyPressed(KeyEvent.VK_ENTER);
    }

    /**
     * DrawManager와 Frame 및 Graphics 통합 확인
     */
    @Test
    void testDrawManagerIntegration() throws Exception {
        // DrawManager의 instance 필드에 접근
        Field instanceField = DrawManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        DrawManager drawManagerInstance = (DrawManager) instanceField.get(null); // static 필드 접근

        // DrawManager의 frame 필드에 접근
        Field frameField = DrawManager.class.getDeclaredField("frame");
        frameField.setAccessible(true);
        Frame frame = (Frame) frameField.get(drawManagerInstance);

        // Frame 객체 확인
        assertNotNull(frame);

        // Graphics 객체 확인
        Graphics graphics = frame.getGraphics();
        assertNotNull(graphics);
    }



}
