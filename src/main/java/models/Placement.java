package models;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "rezerwacaja_miejsca")
public class Placement
{
    private String location_nr;
    private String part_nr;
    private int part_id;
    private int quantity;
    private int user_id;


    public Placement(String location_nr, String part_nr, int part_id, int quantity, int user_id){
        this.location_nr = location_nr;
        this.part_nr = part_nr;
        this.part_id = part_id;
        this.quantity = quantity;
        this.user_id = user_id;

    }

    public void setLocation_nr(String location_nr) {
        this.location_nr = location_nr;
    }

    public void setPart_nr(String part_nr) {
        this.part_nr = part_nr;
    }

    public void setPart_id(int part_id){
    this.part_id = part_id;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getLocation_nr() {
        return location_nr;
    }

    public String getPart_nr() {
        return part_nr;
    }

    public int getPart_id() {
        return part_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getUser_id() {
        return user_id;
    }

    public Placement() {}
}
