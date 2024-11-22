package screen;

import engine.UserManager;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Login screen class.
 */
public class LoginScreen extends Screen {
    private String username = "";
    private String password = "";
    private boolean isUsernameFocused = true;
    private final UserManager userManager;

    private boolean isLoginFailed = false;

    public LoginScreen(final int width, final int height, final int fps, UserManager userManager) {
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
                if (userManager.login(username, password)) {
                    this.returnCode = 1; // 로그인 성공 -> 메인 메뉴
                    this.isRunning = false;
                } else {
                    isLoginFailed = true;
                    username = "";
                    password = "";
                    isUsernameFocused = true;
                }
            }
        } else if (inputManager.isKeyPressed(KeyEvent.VK_F1)) {
            this.returnCode = 9; // F1 키 -> 회원가입 화면 전환
            this.isRunning = false;
        } else if (inputManager.isKeyPressed(KeyEvent.VK_TAB)) {
            isUsernameFocused = !isUsernameFocused; // Toggle focus
        } else if (inputManager.isKeyPressed(KeyEvent.VK_BACK_SPACE)) {
            if (isUsernameFocused && !username.isEmpty()) {
                username = username.substring(0, username.length() - 1);
            } else if (!isUsernameFocused && !password.isEmpty()) {
                password = password.substring(0, password.length() - 1);
            }
        } else {
            char typedChar = inputManager.getTypedKey();
            if (Character.isLetterOrDigit(typedChar)) {
                if (isUsernameFocused) {
                    username += typedChar;
                } else {
                    password += typedChar;
                }
            }
        }

        draw();
    }


    private void draw() {
        drawManager.initDrawing(this);

        // 1. LOGIN 제목 표시
        drawManager.drawTitle(this, "LOGIN");

        // 2. ID 입력 필드와 관련된 텍스트 및 박스 표시
        drawManager.drawCenteredRegularString(this, "ID", this.height / 3 - 20, isUsernameFocused);
        drawManager.drawInputBox(this, username, this.height / 3, isUsernameFocused, Color.GREEN);

        // 3. PW 입력 필드와 관련된 텍스트 및 박스 표시
        drawManager.drawCenteredRegularString(this, "PW", this.height / 3 + 60, !isUsernameFocused);
        drawManager.drawInputBox(this, "*".repeat(password.length()), this.height / 3 + 80, !isUsernameFocused, Color.GREEN);

        // 4. 로그인 실패 메시지 표시
        if (isLoginFailed) {
            drawManager.drawCenteredRegularString(this, "LOGIN FAILED. TRY AGAIN.", this.height / 2 + 40, false);
        }

        // 5. 하단에 회원가입 메시지 표시
        drawManager.drawCenteredRegularString(this, "PRESS F1 TO SIGN UP", this.height - 50, false);

        drawManager.completeDrawing(this);
    }
}
