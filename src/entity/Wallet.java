package entity;

import engine.Core;

import java.io.BufferedReader;
import java.io.IOException;

public class Wallet {
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
        this.bullet_lv = 0;
        this.shot_lv = 0;
        this.lives_lv = 0;
        this.coin_lv = 0;
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
    }

    public void setShot_lv(int shot_lv)
    {
        this.shot_lv = shot_lv;
    }

    public void setLives_lv(int lives_lv)
    {
        this.lives_lv = lives_lv;
    }

    public void setCoin_lv(int coin_lv)
    {
        this.coin_lv = coin_lv;
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
        return true;
    }

    public boolean withdraw(int amount)
    {
        if(amount <= 0) return false;
        if(coin - amount < 0) return false;
        coin -= amount;
        return true;
    }

    //Core.fileManager 등 활용해서, 현재 지갑상태를 파일에 저장하는 메서드 구현필요
    //저장방식: coin, bullet_lv, shot_lv, lives_lv, coin_lv 순으로 한줄씩 저장
    public boolean writeWallet()
    {
        Wallet newWallet = new Wallet(getCoin(), getBullet_lv(), getShot_lv(), getLives_lv(), getCoin_lv());
        try {
            Core.getFileManager().saveWallet(newWallet);
        } catch (IOException e) {
            throw new RuntimeException(e);
            //logger.warning("Couldn't load high scores!");
        }
        return true;
    }

    //Core.fileManager 등 활용해, 파일에 적힌 정보로 지갑 생성하는 메서드 구현필요
    //만약 파일이 손상되어 읽을 수 없다면 초기값(0)으로 생성하기
    public static Wallet getWallet() {
        BufferedReader bufferedReader = null;

        try {
            // FileManager를 통해 파일에서 지갑 데이터를 불러옴
            bufferedReader = Core.getFileManager().loadWallet();

            // 파일이 존재하지 않으면 기본값으로 지갑 생성
            if (bufferedReader == null) {
                Core.getLogger().warning("Wallet file does not exist, initializing with default values.");
                return new Wallet();
            }

            // 파일에서 각 줄을 읽어와서 값 설정
            int coin = Integer.parseInt(bufferedReader.readLine());
            int bullet_lv = Integer.parseInt(bufferedReader.readLine());
            int shot_lv = Integer.parseInt(bufferedReader.readLine());
            int lives_lv = Integer.parseInt(bufferedReader.readLine());
            int coin_lv = Integer.parseInt(bufferedReader.readLine());

            // 읽어온 값으로 새로운 Wallet 객체를 생성해 반환
            return new Wallet(coin, bullet_lv, shot_lv, lives_lv, coin_lv);

        } catch (IOException | NumberFormatException e) {
            // 파일을 읽지 못하거나 손상된 경우 기본값으로 반환, 에러 로깅
            Core.getLogger().severe("Error loading wallet data: " + e.getMessage());
            return new Wallet();
        } finally {
            // 파일 리소스 해제
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Core.getLogger().severe("Error closing wallet file: " + e.getMessage());
                }
            }
        }
    }

}
