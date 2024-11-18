package screen;

import engine.UserManager;

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
            isUsernameFocused = !isUsernameFocused; // 포커스 전환 (Username ↔ Password)
        }  else if (inputManager.isKeyPressed(KeyEvent.VK_BACK_SPACE)) {
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
        drawManager.drawCenteredText(this, "Register", 100);
        drawManager.drawCenteredText(this, "Username: " + username, 200);
        drawManager.drawCenteredText(this, "Password: " + "*".repeat(password.length()), 250);
        drawManager.drawCenteredText(this, isUsernameFocused ? ">>" : "  ", 200); // 포커스 표시
        if (isRegisterFailed) {
            // 회원가입 실패 메시지 표시
            drawManager.drawCenteredText(this, "Register failed. Try again.", 130);
        }
        drawManager.completeDrawing(this);
    }
}
