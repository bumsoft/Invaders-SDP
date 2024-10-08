package screen;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;

import engine.Core;

public class CreditScreen extends Screen{

    private int currentFrame;
    private List<String> creditlist;

    public CreditScreen(final int width, final int height, final int fps){
        super(width, height, fps);

        this.returnCode = 1;
        this.currentFrame = 0;

        try{
            this.creditlist = Core.getFileManager().loadCreditList();
            logger.info(""+this.creditlist);
        }  catch (NumberFormatException | IOException e) {
            logger.warning("Couldn't load credit list!");
        }



    }
    public int run(){
        super.run();

        return this.returnCode;
    }

//    private final ArrayList loadcredit(){
//
//
//
//    }

    protected final void update() {
        super.update();
        currentFrame++;

        if (currentFrame > 50 * 60) {//임시로 50초
            this.isRunning = false;
            this.returnCode = 1;
        }

        draw();
        if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)
                && this.inputDelay.checkFinished())
            this.isRunning = false;
    }

    private void draw(){
        drawManager.initDrawing(this);
        drawManager.drawEndingCredit(this,this.creditlist, currentFrame);
        drawManager.completeDrawing(this);
    }

}
