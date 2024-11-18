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
        // Check for input
        if (inputManager.isKeyPressed(KeyEvent.VK_ENTER)) {
            if(isUsernameFocused)
            {
                isUsernameFocused = false;
                logger.info("Username: " + username);
            }
            // Submit registration data
            else this.isRunning = false; // Exit screen
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
            if (typedChar >='a' && typedChar <= 'z'
                || typedChar >='A' && typedChar <= 'Z'
                || typedChar >='0' && typedChar <=9) {
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
        drawManager.drawCenteredText(this, "Register Screen", 100);
        drawManager.drawCenteredText(this, "Username: " + username, 200);
        drawManager.drawCenteredText(this, "Password: " + "*".repeat(password.length()), 250);
        drawManager.drawCenteredText(this, isUsernameFocused ? ">>" : "  ", 200); // Indicate focus
        drawManager.completeDrawing(this);
    }
}
