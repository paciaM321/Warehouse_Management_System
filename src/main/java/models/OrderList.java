package models;

import javax.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "order_id")
    private int orderId;

    @Column(name = "part_id")
    private int partId;

    private int quantity;
    private int submit;

    public OrderList() {}

    public OrderList(int orderId, int partId, int quantity) {
        this.orderId = orderId;
        this.partId = partId;
        this.quantity = quantity;
    }



    // Gettery i Settery
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getPartId() { return partId; }
    public void setPartId(int partId) { this.partId = partId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getSubmit() {
        return submit;
    }
    public void setSubmit(int submit) {
        this.submit = submit;
    }
}