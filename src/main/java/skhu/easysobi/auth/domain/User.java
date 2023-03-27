package skhu.easysobi.auth.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import skhu.easysobi.common.BaseTime;
import skhu.easysobi.inventory.domain.UserInventory;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class User extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true)
    private String email;

    private String nickname;

    @Column(unique = true)
    private Long kakaoId;

    @OneToMany(mappedBy = "user")
    private List<UserInventory> userInventoryList = new ArrayList<>(); // 식품 목록

}
