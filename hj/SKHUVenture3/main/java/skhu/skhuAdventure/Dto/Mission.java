package skhu.skhuAdventure.Dto;

import lombok.Data;

import java.math.BigInteger;

@Data
public class Mission {
    private BigInteger id;
    private String userId;  // 외래키 참조를 위한 필드
    private Integer buildingId;  // 외래키 참조를 위한 필드
    private Integer complete;
}