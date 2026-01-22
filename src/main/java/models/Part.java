package models;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "part")

public class Part {
    private String part_nr;
    private int part_id;
    private String part_name;
    private Long quantity;
    private int by_user_id;
    private String status;
    private Date created_at;

    public Part(String part_nr,int part_id, String part_name, Long quantity, int by_user_id, String status, Date created_at){

        this.part_nr = part_nr;
        this.part_id = part_id;
        this.part_name = part_name;
        this.quantity = quantity;
        this.by_user_id = by_user_id;
        this.status = status;
        this.created_at = created_at;
    }
    public String getPart_nr() {return part_nr;}
    public int getPart_id() {return part_id;}
    public String getPart_name() {return part_name;}
    public Long getQuantity() {return quantity;}
    public int getBy_user_id() {return by_user_id;}
    public Date getCreated_at() {return created_at;}
    public String getStatus() {return status;}

    public void setPart_nr(String part_nr) {
        this.part_nr = part_nr;
    }

    public void setPart_id(int part_id) {
        this.part_id = part_id;
    }

    public void setPart_name(String part_name) {
        this.part_name = part_name;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public void setBy_user_id(int by_user_id) {
        this.by_user_id = by_user_id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Part() {}

///location : 1-8/1-10/1-5
}
