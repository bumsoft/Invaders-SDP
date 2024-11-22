package screen.PVP;

import java.util.List;

public class GameStateDTO {

    private int player1X;
    private int player1Y;
    private List<BulletPositionDTO> player1BulletPositionDTO;

    private int player2X;
    private int player2Y;
    private List<BulletPositionDTO> player2BulletPositionDTO;

    public GameStateDTO()
    {
    }

    public int getPlayer1X()
    {
        return player1X;
    }

    public void setPlayer1X(int player1X)
    {
        this.player1X = player1X;
    }

    public int getPlayer1Y()
    {
        return player1Y;
    }

    public void setPlayer1Y(int player1Y)
    {
        this.player1Y = player1Y;
    }

    public List<BulletPositionDTO> getPlayer1BulletPositionDTO()
    {
        return player1BulletPositionDTO;
    }

    public void setPlayer1BulletPositionDTO(List<BulletPositionDTO> player1BulletPositionDTO)
    {
        this.player1BulletPositionDTO = player1BulletPositionDTO;
    }

    public int getPlayer2X()
    {
        return player2X;
    }

    public void setPlayer2X(int player2X)
    {
        this.player2X = player2X;
    }

    public int getPlayer2Y()
    {
        return player2Y;
    }

    public void setPlayer2Y(int player2Y)
    {
        this.player2Y = player2Y;
    }

    public List<BulletPositionDTO> getPlayer2BulletPositionDTO()
    {
        return player2BulletPositionDTO;
    }

    public void setPlayer2BulletPositionDTO(List<BulletPositionDTO> player2BulletPositionDTO)
    {
        this.player2BulletPositionDTO = player2BulletPositionDTO;
    }
}
