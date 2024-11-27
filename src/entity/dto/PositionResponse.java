package entity.dto;

public class PositionResponse {
    private int playerX;
    private int playerY;
    private int enemyPlayerX;
    private int enemyPlayerY;

    public PositionResponse()
    {
    }

    public int getPlayerX()
    {
        return playerX;
    }

    public void setPlayerX(int playerX)
    {
        this.playerX = playerX;
    }

    public int getPlayerY()
    {
        return playerY;
    }

    public void setPlayerY(int playerY)
    {
        this.playerY = playerY;
    }

    public int getEnemyPlayerX()
    {
        return enemyPlayerX;
    }

    public void setEnemyPlayerX(int enemyPlayerX)
    {
        this.enemyPlayerX = enemyPlayerX;
    }

    public int getEnemyPlayerY()
    {
        return enemyPlayerY;
    }

    public void setEnemyPlayerY(int enemyPlayerY)
    {
        this.enemyPlayerY = enemyPlayerY;
    }
}