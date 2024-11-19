package entity.dto;

public class UserScoreDto {
    private String username;
    private Long score;

    public UserScoreDto()
    {
    }

    public UserScoreDto(String username, Long score)
    {
        this.username = username;
        this.score = score;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public Long getScore()
    {
        return score;
    }

    public void setScore(Long score)
    {
        this.score = score;
    }
}
