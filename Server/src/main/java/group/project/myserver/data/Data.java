package group.project.myserver.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@lombok.Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_data")
public class Data {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_data")
    private Integer id_data;
    @Column(name = "id_user")
    private Integer id_user;
    @Column(name = "heart_rate")
    private float heart_rate;
    @Column(name = "steps")
    private Integer steps;
    @Column(name = "time_stamp")
    private LocalDateTime time_stamp;

}
