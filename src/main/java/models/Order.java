package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "order")
public class Order {
    @Id
    private int  order_id;
    private String company;
    private String order_list_id;
    private int user_id;

    public Order(int order_id, String company, String order_list_id,int user_id){

        this.order_id = order_id;
        this.company = company;
        this.order_list_id = order_list_id;
        this.user_id = user_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setOrder_list_id(String order_list_id) {
        this.order_list_id = order_list_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public String getCompany() {
        return company;
    }

    public String getOrder_list_id() {
        return order_list_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public Order() {}
}
