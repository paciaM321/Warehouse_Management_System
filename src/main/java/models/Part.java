package models;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;

@Entity
@Table(name = "parts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Part {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "part_number", nullable = false)
    private String partNr;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private long quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartStatus status;

    @Column(unique = true)
    private String location;

    public Part(String partNr, String name, long quantity) {
        this.partNr = partNr;
        this.name = name;
        this.quantity = quantity;
        this.status = PartStatus.PROCESSING;
    }
}