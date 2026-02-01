package controllers.OrderPanelsControllers;

public class OrderListDTO {
    private int orderId;
    private String client;
    private String partNr;
    private String location;
    private int quantity;
    private String status;

    public OrderListDTO(int orderId, String client, String partNr, String location, int quantity, String status) {
        this.orderId = orderId;
        this.client = client;
        this.partNr = partNr;
        this.location = location;
        this.quantity = quantity;
        this.status = status;
    }
    // Dodaj Gettery dla wszystkich pól! (wymagane przez PropertyValueFactory)
}