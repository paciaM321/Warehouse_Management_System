package models;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "order_list")
public class OrderList {

    private int id;
    private String location_nr;
    private String part_nr;
    private int quantity;
    private int user_id;
    private int submit;

    public OrderList(int id, String location_nr,String part_nr,int user_id, int quantity,int submit){
        this.id = id;
        this.location_nr = location_nr;
        this.part_nr = part_nr;
        this.user_id = user_id;
        this.quantity = quantity;
        this.submit = submit;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPart_nr(String part_nr) {
        this.part_nr = part_nr;
    }

    public void setLocation_nr(String location_nr) {
        this.location_nr = location_nr;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setSubmit(int submit) {
        this.submit = submit;
    }

    public int getId() {
        return id;
    }

    public String getLocation_nr() {
        return location_nr;
    }

    public String getPart_nr() {
        return part_nr;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getSubmit() {
        return submit;
    }
}
