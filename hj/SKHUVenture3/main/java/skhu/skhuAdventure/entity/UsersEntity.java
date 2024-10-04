package skhu.skhuAdventure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "users")
public class UsersEntity {

    @Column(name = "password")
    private String password;

    @Column(name = "studentId")
    private Integer studentId;

    @Column(name = "profileImageName")
    private String profileImageName;

    @Column(name = "profileImagePath")
    private String profileImagePath;

    @Id
    @Column(name = "userId")
    private String userId;

    @Column(name = "userName")
    private String userName;

    @ManyToOne
    @JoinColumn(name = "major_id", referencedColumnName = "majorId")
    private MajorEntity major;
}

