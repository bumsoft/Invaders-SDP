package socket;

public class Responses {
    private boolean isGameStarted = false;
    private boolean isRoomCreated = false;
    private String roomCode = "";

    private boolean isGameJoin = false;
    private String opponent = "";

    public boolean isGameJoin()
    {
        return isGameJoin;
    }

    public void setGameJoin(boolean gameJoin)
    {
        isGameJoin = gameJoin;
    }

    public String getOpponent()
    {
        return opponent;
    }

    public void setOpponent(String opponent)
    {
        this.opponent = opponent;
    }

    public boolean isGameStarted()
    {
        return isGameStarted;
    }

    public void setGameStarted(boolean gameStarted)
    {
        isGameStarted = gameStarted;
    }

    public boolean isRoomCreated()
    {
        return isRoomCreated;
    }

    public void setRoomCreated(boolean roomCreated)
    {
        isRoomCreated = roomCreated;
    }

    public String getRoomCode()
    {
        return roomCode;
    }

    public void setRoomCode(String roomCode)
    {
        this.roomCode = roomCode;
    }

    public void reset()
    {
        isGameStarted = false;
        isRoomCreated = false;
        roomCode = "";
        isGameJoin = false;
        opponent = "";
    }
}
