package screen;

import engine.UserManager;

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
        drawManager.drawCenteredText(this, "Login", 100);
        drawManager.drawCenteredText(this, "Username: " + username, 200);
        drawManager.drawCenteredText(this, "Password: " + "*".repeat(password.length()), 250);
        if (isLoginFailed)
            drawManager.drawCenteredText(this, "Login failed. Try again.", 130);

        // Display instructions at the bottom
        drawManager.drawCenteredText(this, "Press \"F1\" to Sign Up", this.height - 50);

        drawManager.completeDrawing(this);
    }
}
