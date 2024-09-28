package entity;

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
    public boolean writeWallet()
    {
        return true;
    }

    //Core.fileManager 등 활용해, 파일에 적힌 정보로 지갑 생성하는 메서드 구현필요
    //만약 파일이 손상되어 읽을 수 없다면 초기값(0)으로 생성하기
    public static Wallet getWallet()
    {
        return new Wallet();
    }
}
