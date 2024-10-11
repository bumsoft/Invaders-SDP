package entity;

import engine.Core;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;

public class Wallet {
    private static Logger logger = Core.getLogger();
    private int coin;

    //bullet speed level
    private int bullet_lv;

    //shot frequency level
    private int shot_lv;

    //additional lives level
    private int lives_lv;

    //coin gain level
    private int coin_lv;

    public Wallet()
    {
        this.coin = 0;
        this.bullet_lv = 1;
        this.shot_lv = 1;
        this.lives_lv = 1;
        this.coin_lv = 1;
        writeWallet();
    }

    public Wallet(int coin, int bullet_lv, int shot_lv, int lives_lv, int coin_lv)
    {
        this.coin = coin;
        this.bullet_lv = bullet_lv;
        this.shot_lv = shot_lv;
        this.lives_lv = lives_lv;
        this.coin_lv = coin_lv;
    }

    public int getCoin()
    {
        return coin;
    }

    public int getBullet_lv()
    {
        return bullet_lv;
    }

    public int getShot_lv()
    {
        return shot_lv;
    }

    public int getLives_lv()
    {
        return lives_lv;
    }

    public int getCoin_lv()
    {
        return coin_lv;
    }

    public void setBullet_lv(int bullet_lv)
    {
        this.bullet_lv = bullet_lv;
        writeWallet();
        logger.info("Upgrade Bullet Speed " + (bullet_lv-1) + "to " + bullet_lv);
    }

    public void setShot_lv(int shot_lv)
    {
        this.shot_lv = shot_lv;
        writeWallet();
        logger.info("Upgrade Shop Frequency  " + (shot_lv-1) + "to " + shot_lv);
    }

    public void setLives_lv(int lives_lv)
    {
        this.lives_lv = lives_lv;
        writeWallet();
        logger.info("Upgrade Additional Lives " + (lives_lv-1) + "to " + lives_lv);
    }

    public void setCoin_lv(int coin_lv)
    {
        this.coin_lv = coin_lv;
        writeWallet();
        logger.info("Upgrade Gain Coin " + (coin_lv-1) + "to " + coin_lv);
    }

    public boolean deposit(int amount)
    {
        if(amount <= 0) return false;
        if(coin > Integer.MAX_VALUE -amount)
        {
            coin = Integer.MAX_VALUE;
            return true;
        }
        coin += amount;
        writeWallet();
        logger.info("Deposit completed. Your coin: " + this.coin);
        return true;
    }

    public boolean withdraw(int amount)
    {
        if(amount <= 0) return false;
        if(coin - amount < 0)
        {
            logger.info("Insufficient coin");
            return false;
        }
        coin -= amount;
        writeWallet();
        logger.info("Withdraw completed. Your coin: " + this.coin);
        return true;
    }

    //현재 지갑상태를 파일에 저장. 저장방식: coin, bullet_lv, shot_lv, lives_lv, coin_lv 순으로 한줄씩 저장
    //Save the current wallet state to a file. Save format: coin, bullet_lv, shot_lv, lives_lv, coin_lv, each in one line
    private void writeWallet()
    {
        try {
            Core.getFileManager().saveWallet(this);
        } catch (IOException e) {
//            throw new RuntimeException(e);
            logger.warning("Couldn't load wallet!");
        }
    }

    // 파일에 적힌 정보로 지갑 생성. 만약 파일이 손상되어 읽을 수 없다면 초기값(0)으로 생성하기
    //Create a wallet using the information written in the file. If the file is damaged or unreadable, create it with initial values (0)
    public static Wallet getWallet() {
        BufferedReader bufferedReader = null;

        try {
            //FileManager를 통해 파일에서 지갑 데이터를 불러옴
            //Load wallet data from the file via FileManager
            bufferedReader = Core.getFileManager().loadWallet();

            if (bufferedReader == null) {
                logger.info("Wallet file does not exist, initializing with default values.");

                return new Wallet();
            }

            //파일에서 각 줄을 읽어와서 값 설정
            //Read each line from the file and set the values
            int coin = Integer.parseInt(bufferedReader.readLine());
            int levelSeq [] = new int[4]; //bullet_lv, shot_lv, lives_lv, coin_lv
            for (int i = 0; i < 4; i++) {
                int level = Integer.parseInt(bufferedReader.readLine());
                if(level > 4 || level <= 0){
                    logger.info("Weird level. Initializing with default values.");
                    return new Wallet();
                }
                levelSeq[i] = level;
            }

            return new Wallet(coin, levelSeq[0], levelSeq[1], levelSeq[2], levelSeq[3]);

        } catch (IOException | NumberFormatException e) {
            //파일을 읽지 못하거나 손상된 경우 기본값으로 반환
            //If there is an error reading the file or if it's corrupted, return with default values
            logger.info("Error loading wallet data. Initializing with default values.");
            return new Wallet();
        } finally {
            //파일 리소스 해제
            //Release file resources
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}