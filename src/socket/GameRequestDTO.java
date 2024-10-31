package socket;

public class GameRequestDTO {
    private String order;

    public GameRequestDTO(String order) {
        this.order = order;
    }

    public String getOrder()
    {
        return order;
    }

    public void setOrder(String order)
    {
        this.order = order;
    }
}
