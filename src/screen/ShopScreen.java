package screen;

import java.awt.event.KeyEvent;

import engine.Cooldown;
import engine.Core;
import entity.Wallet;

public class ShopScreen extends Screen {

    /** Milliseconds between changes in user selection. */
    private static final int SELECTION_TIME = 200;

    /** Time between changes in user selection. */
    private Cooldown selectionCooldown;

    private Wallet wallet;

    /** 1-bullet speed 2-shot frequency 3-additional lives 4-gain coin upgrade */
    private int selected_item;

    //레벨별 코스트
    private int lv1cost = 2000;
    private int lv2cost = 4000;
    private int lv3cost = 8000;

    //예외처리 변수
    private boolean lesscoin = false;
    private boolean overlv = false;

    //경고문구동안 조작금지위한 타이머
    private boolean timer = false;
    private int count = 0;
    private int maxcount = 100;


    /**
     * Constructor, establishes the properties of the screen.
     *
     * @param width
     *            Screen width.
     * @param height
     *            Screen height.
     * @param fps
     *            Frames per second, frame rate at which the game is run.
     */
    public ShopScreen(final int width, final int height, final int fps, final Wallet wallet) {
        super(width, height, fps);

        // Defaults to play.
        this.returnCode = 2;
        this.selectionCooldown = Core.getCooldown(SELECTION_TIME); //200밀리초를 갖는 cooldown객체 생성해서 반환
        this.selectionCooldown.reset(); // cooldown의 시작시간을 현재시간으로 설정
        this.wallet = wallet;
        selected_item = 1;
    }

    /**
     * Starts the action.
     *
     * @return Next screen code(MainMenu).
     */
    public final int run() {
        super.run();

        return 1; //메뉴화면으로
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    protected final void update() {
        super.update();

        draw();
        if (this.selectionCooldown.checkFinished() //마지막 동작으로부터 0.2초가 지났고 &&
                && this.inputDelay.checkFinished()) { //객체생성으로부터 1초 지났다면. (매 입력이 아닌 그냥 스크린 전환후 1초가 지났는지)

            //윗키 또는 W 누르면 위 선택지로 returnCode변경
            if (inputManager.isKeyDown(KeyEvent.VK_UP)
                    || inputManager.isKeyDown(KeyEvent.VK_W)) {
                previousMenuItem();
                this.selectionCooldown.reset();//동작 뒤 현재시간 갱신
            }
            //아랫키 또는 S 누르면 아래 선택지로 returnCode변경
            if (inputManager.isKeyDown(KeyEvent.VK_DOWN)
                    || inputManager.isKeyDown(KeyEvent.VK_S)) {
                nextMenuItem();
                this.selectionCooldown.reset();//동작 뒤 현재시간 갱신
            }

            if (inputManager.isKeyDown(KeyEvent.VK_SPACE) && !this.timer)
            {
                //화폐 처리 및 레벨 처리
                switch (selected_item) {
                    case 1:
                        if (usecoinforupgrade(wallet.getBullet_lv())) {
                            wallet.setBullet_lv(wallet.getBullet_lv() + 1);
                        }
                        break;
                    case 2:
                        if (usecoinforupgrade(wallet.getShot_lv())) {
                            wallet.setShot_lv(wallet.getShot_lv() + 1);
                        }
                        break;
                    case 3:
                        if (usecoinforupgrade(wallet.getLives_lv())) {
                            wallet.setLives_lv(wallet.getLives_lv() + 1);
                        }
                        break;
                    case 4:
                        if (usecoinforupgrade(wallet.getCoin_lv())) {
                            wallet.setCoin_lv(wallet.getCoin_lv() + 1);
                        }
                        break;
                    default:
                        break;
                }
            }
            this.selectionCooldown.reset();//동작 뒤 현재시간 갱신

            //esc누르면 running false
            if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE) && !this.timer)
                this.isRunning = false;

            //조작정지를 위한 타이머
            if(this.timer)
            {
                if(this.count == this.maxcount)
                {
                    this.timer = false;
                    this.count = 0;
                    if(this.lesscoin)
                    {
                        this.lesscoin = false;
                        logger.info("made false");
                    }
                    else if(this.overlv)
                    {
                        this.overlv = false;
                        logger.info("made lv false");
                    }
                }
                else
                {
                    this.count += 1;
                    logger.info("timer : "+this.count);
                }
            }
        }
    }

    /**
     * Shifts the focus to the next menu item.
     */
    //맨 아래서 또 아래 누르면 처음으로 이동하는 등의 로직 + 2->3->0->2->3->0반복!
    private void nextMenuItem() {
        if (this.selected_item == 4)
            this.selected_item = 1;
        else
            this.selected_item++;
    }

    /**
     * Shifts the focus to the previous menu item.
     */
    //맨 위에서 또 위 누르면 마지막으로 이동하는 등의 로직
    private void previousMenuItem() {
        if (this.selected_item == 1)
            this.selected_item = 4;
        else
            this.selected_item--;
    }

    private void draw() {
        drawManager.initDrawing(this);


        drawManager.drawShop(this,selected_item,wallet,this.lesscoin,this.overlv);

        drawManager.completeDrawing(this);
    }

    public boolean usecoinforupgrade(int level)
    {
        if(level == 1)
        {
            if(wallet.withdraw(lv1cost))
            {
                logger.info("currentcoin : " + wallet.getCoin());
                return true;
            }
            else
            {
                logger.info("currentcoin : " + wallet.getCoin());
                this.lesscoin = true;
                logger.info("made true");
                this.timer = true;
                return false;
            }
        }
        else if(level == 2)
        {
            if(wallet.withdraw(lv2cost))
            {
                logger.info("currentcoin : " + wallet.getCoin());
                return true;
            }
            else
            {
                logger.info("currentcoin : " + wallet.getCoin());
                this.lesscoin = true;
                logger.info("made true");
                this.timer = true;
                return false;
            }
        }
        else if(level == 3)
        {
            if(wallet.withdraw(lv3cost))
            {
                logger.info("currentcoin : " + wallet.getCoin());
                return true;
            }
            else
            {
                logger.info("currentcoin : " + wallet.getCoin());
                this.lesscoin = true;
                logger.info("made true");
                this.timer = true;
                return false;
            }
        }
        else if(level==4)
        {
            logger.info("over lv");
            this.overlv = true;
            logger.info("made lv true");
            this.timer = true;
            return false;
        }
        logger.info("currentcoin : " + wallet.getCoin());
        return false;
    }
}
