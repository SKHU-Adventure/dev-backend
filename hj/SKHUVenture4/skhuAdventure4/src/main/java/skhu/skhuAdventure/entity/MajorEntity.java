package skhu.skhuAdventure.entity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "major")
public class MajorEntity {


    @Id
    @Column(name = "majorId", nullable = false)
    private Integer majorId;

    @Column(name = "majorName", nullable = true)
    private String majorName;

}