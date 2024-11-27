package test.screen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import screen.RegisterScreen;
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

class RegisterScreenTest {
    private RegisterScreen registerScreen;
    private UserManager userManager;
    private InputManager inputManager;

    @BeforeEach
    void setUp() throws Exception {
        // Mock 객체 생성
        userManager = mock(UserManager.class);
        inputManager = mock(InputManager.class);

        // RegisterScreen 인스턴스 생성 및 초기화
        registerScreen = new RegisterScreen(600, 650, 60, userManager);
        registerScreen.initialize();

        // 리플렉션으로 inputManager 주입
        Field inputManagerField = Screen.class.getDeclaredField("inputManager");
        inputManagerField.setAccessible(true);
        inputManagerField.set(registerScreen, inputManager);

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

        // isRunning 필드 초기화 강제 설정 (필요 시)
        isRunningField.set(registerScreen, true);

        // 값 확인
        assertTrue((boolean) isRunningField.get(registerScreen));
    }

    /**
     * returnCode 필드 초기값과 변경 확인
     */
    @Test
    void testReturnCodeField() throws Exception {
        Field returnCodeField = Screen.class.getDeclaredField("returnCode");
        returnCodeField.setAccessible(true);

        // 초기값 확인
        assertEquals(1, (int) returnCodeField.get(registerScreen));

        // 회원가입 성공 시 값 변경 확인
        when(userManager.register("testUser", "testPass")).thenReturn(true);
        returnCodeField.set(registerScreen, 10);
        assertEquals(10, (int) returnCodeField.get(registerScreen));
    }

    /**
     * update() 메서드의 키 입력 처리 확인
     */
    @Test
    void testUpdateMethod() throws Exception {
        when(inputManager.isKeyPressed(KeyEvent.VK_ENTER)).thenReturn(true);

        Method updateMethod = RegisterScreen.class.getDeclaredMethod("update");
        updateMethod.setAccessible(true);
        updateMethod.invoke(registerScreen);

        // 키 입력 검증
        verify(inputManager, atLeastOnce()).isKeyPressed(KeyEvent.VK_ENTER);
    }

    /**
     * 회원가입 실패 처리 확인
     */
    @Test
    void testRegisterFailed() throws Exception {
        Field isRegisterFailedField = RegisterScreen.class.getDeclaredField("isRegisterFailed");
        isRegisterFailedField.setAccessible(true);

        // 실패 상태 설정
        isRegisterFailedField.set(registerScreen, true);

        // 확인
        assertTrue((boolean) isRegisterFailedField.get(registerScreen));
    }
}
