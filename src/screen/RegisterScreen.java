package screen;

import engine.UserManager;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Register screen class.
 */
public class RegisterScreen extends Screen {
    private String username = "";
    private String password = "";
    private boolean isUsernameFocused = true;
    private final UserManager userManager;
    private boolean isRegisterFailed = false; // 회원가입 실패 여부

    public RegisterScreen(final int width, final int height, final int fps, final UserManager userManager) {
        super(width, height, fps);
        this.userManager = userManager;
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
        if (inputManager.isKeyPressed(KeyEvent.VK_ENTER)) {
            if (isUsernameFocused) {
                isUsernameFocused = false; // Move focus to password
            } else {
                boolean registerSuccess = userManager.register(username, password);
                if (registerSuccess) {
                    this.returnCode = 10; // 회원가입 성공 -> 로그인 화면으로 전환
                    this.isRunning = false;
                } else {
                    isRegisterFailed = true;
                    username = "";
                    password = "";
                    isUsernameFocused = true;
                }
            }
        } else if (inputManager.isKeyPressed(KeyEvent.VK_TAB)) {
            isUsernameFocused = !isUsernameFocused; // Toggle focus
        } else if (inputManager.isKeyPressed(KeyEvent.VK_BACK_SPACE)) {
            // 입력값 삭제
            if (isUsernameFocused && !username.isEmpty()) {
                username = username.substring(0, username.length() - 1);
            } else if (!isUsernameFocused && !password.isEmpty()) {
                password = password.substring(0, password.length() - 1);
            }
        } else {
            // 문자 입력 처리
            char typedChar = inputManager.getTypedKey();
            if (Character.isLetterOrDigit(typedChar)) {
                if (isUsernameFocused) {
                    username += typedChar;
                } else {
                    password += typedChar;
                }
            }
        }

        // Draw screen
        draw();
    }

    private void draw() {
        drawManager.initDrawing(this);

        // 1. REGISTER 제목 표시
        drawManager.drawTitle(this, "REGISTER");

        // 2. ID 입력 필드와 관련된 텍스트 및 박스 표시
        drawManager.drawCenteredRegularString(this, "ID", this.height / 3 - 20, isUsernameFocused);
        drawManager.drawInputBox(this, username, this.height / 3, isUsernameFocused, Color.GREEN);

        // 3. PW 입력 필드와 관련된 텍스트 및 박스 표시
        drawManager.drawCenteredRegularString(this, "PW", this.height / 3 + 60, !isUsernameFocused);
        drawManager.drawInputBox(this, "*".repeat(password.length()), this.height / 3 + 80, !isUsernameFocused, Color.GREEN);

        // 4. 회원가입 실패 메시지 표시
        if (isRegisterFailed) {
            drawManager.drawCenteredRegularString(this, "REGISTER FAILED. TRY AGAIN.", this.height / 2 + 40, false);
        }

        // 5. 하단에 회원가입 메시지 표시
        drawManager.drawCenteredRegularString(this, "PRESS ENTER TO SUBMIT", this.height - 50, false);

        drawManager.completeDrawing(this);
    }
}
