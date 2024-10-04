package skhu.skhuAdventure.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "building")
public class BuildingEntity {
    @Id
    private Integer id;

    private String buildingName;
}