package screen.PVP;

import java.util.List;

public class GameStateDTO {

    private int p1X;
    private int p1Y;
    private List<BulletPositionDTO> p1b;

    private int p2X;
    private int p2Y;
    private List<BulletPositionDTO> p2b;

    public GameStateDTO()
    {
    }

    public int getP1X()
    {
        return p1X;
    }

    public void setP1X(int p1X)
    {
        this.p1X = p1X;
    }

    public int getP1Y()
    {
        return p1Y;
    }

    public void setP1Y(int p1Y)
    {
        this.p1Y = p1Y;
    }

    public List<BulletPositionDTO> getP1b()
    {
        return p1b;
    }

    public void setP1b(List<BulletPositionDTO> p1b)
    {
        this.p1b = p1b;
    }

    public int getP2X()
    {
        return p2X;
    }

    public void setP2X(int p2X)
    {
        this.p2X = p2X;
    }

    public int getP2Y()
    {
        return p2Y;
    }

    public void setP2Y(int p2Y)
    {
        this.p2Y = p2Y;
    }

    public List<BulletPositionDTO> getP2b()
    {
        return p2b;
    }

    public void setP2b(List<BulletPositionDTO> p2b)
    {
        this.p2b = p2b;
    }
}
