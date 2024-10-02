package group.project.myserver.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user_extra")
public class UserExtra {

    @Id
    @Column(name = "user_id")
    private Integer id;

    @OneToOne
    @MapsId // This annotation will use the userId as both the primary key and the foreign key
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "age", nullable = true)
    private Integer age;

    @Column(name = "height", nullable = true)
    private Integer height;

    @Column(name = "weight", nullable = true)
    private Float weight;

    @Column(name = "country", nullable = true)
    private String country;

    @Column(name = "city", nullable = true)
    private String city;

    @Column(name = "street", nullable = true)
    private String street;

    @Column(name = "phone", nullable = true)
    private String phone;
}
