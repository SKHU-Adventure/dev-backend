package skhu.skhuAdventure.Dto;

import lombok.Data;

import java.math.BigInteger;

@Data
public class Image {
    private BigInteger id;
    private BigInteger imageNumber;
    private String imageName;
    private String imagePath;
    private String userId;  // 외래키 참조를 위한 필드
}
