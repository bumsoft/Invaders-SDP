package screen;

import java.awt.event.KeyEvent;

/**
 * Login screen class.
 */
public class LoginScreen extends Screen {
    private String username = "";
    private String password = "";
    private boolean isUsernameFocused = true;

    public LoginScreen(final int width, final int height, final int fps) {
        super(width, height, fps);
    }

    @Override
    public void initialize() {
        super.initialize();
        this.returnCode = 0; // Default return code
    }

    @Override
    protected void update() {
        // Check for input
        if (inputManager.isKeyPressed(KeyEvent.VK_ENTER)) {
            // Submit login data
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);
            this.isRunning = false; // Exit screen
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
            if (typedChar != '\0') {
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
        drawManager.drawCenteredText(this, "Login Screen", 100);
        drawManager.drawCenteredText(this, "Username: " + username, 200);
        drawManager.drawCenteredText(this, "Password: " + "*".repeat(password.length()), 250);
        drawManager.drawCenteredText(this, isUsernameFocused ? ">>" : "  ", 200); // Indicate focus
        drawManager.completeDrawing(this);
    }
}
