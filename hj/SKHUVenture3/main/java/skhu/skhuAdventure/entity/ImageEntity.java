package skhu.skhuAdventure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "image")
public class ImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name = "imageNumber")
    private BigInteger imageNumber;

    @Column(name = "imageName")
    private String imageName;

    @Column(name = "imagePath")
    private String imagePath;

    @Column(name = "user_id")
    private String userId;
}