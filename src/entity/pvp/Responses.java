package entity.pvp;

import entity.dto.GameStateDTO;

public class Responses {

    //before game starts
    private boolean isGameStarted = false;
    private boolean isRoomCreated = false;
    private String roomCode = "";
    private boolean isGameJoin = false;
    private String opponent = "";
    private boolean isOpponentReady = false;
    private boolean isRoomOwner = false;

    //after game starts
    private boolean isGameOver = false;
    private boolean isWin = false;
    private boolean isError = false;

    GameStateDTO gameStateDTO;

    public boolean isError()
    {
        return isError;
    }

    public void setError(boolean error)
    {
        isError = error;
    }

    public GameStateDTO getGameStateDTO()
    {
        return gameStateDTO;
    }

    public void setGameStateDTO(GameStateDTO gameStateDTO)
    {
        this.gameStateDTO = gameStateDTO;
    }

    public boolean isGameOver()
    {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver)
    {
        isGameOver = gameOver;
    }

    public boolean isWin()
    {
        return isWin;
    }

    public void setWin(boolean win)
    {
        isWin = win;
    }

    public boolean isOpponentReady()
    {
        return isOpponentReady;
    }

    public void setOpponentReady(boolean opponentReady)
    {
        isOpponentReady = opponentReady;
    }

    public boolean isRoomOwner()
    {
        return isRoomOwner;
    }

    public void setRoomOwner(boolean roomOwner)
    {
        isRoomOwner = roomOwner;
    }

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
}
