package models;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int submit;

    public OrderList(Order order, Part part, int quantity) {
        this.order = order;
        this.part = part;
        this.quantity = quantity;
        this.submit = 0;
    }

    public int getOrderId() {
        return order != null ? order.getId() : 0;
    }

    public int getPartId() {
        return part != null ? part.getId() : 0;
    }
}