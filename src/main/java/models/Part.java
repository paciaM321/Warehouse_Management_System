package models;

import javax.persistence.*;

@Entity
@Table(name = "parts")
public class Part {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "part_number")
    private String partNr;

    private String name;
    private long quantity;
    private String status;
    private String location;

    public Part() {}

    public Part(String partNr, String name, long quantity) {
        this.partNr = partNr;
        this.name = name;
        this.quantity = quantity;
        this.status = "PROCESSING";
    }

    // Gettery i Settery
    public int getId() { return id; }
    public String getPartNr() { return partNr; }
    public String getName() { return name; }
    public long getQuantity() { return quantity; }
    public String getStatus() { return status; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public void setId(int id) { this.id = id; }
    public void setPartNr(String partNr) { this.partNr = partNr; }
    public void setName(String name) { this.name = name; }
    public void setQuantity(long quantity) { this.quantity = quantity; }
    public void setStatus(String status) { this.status = status; }

}