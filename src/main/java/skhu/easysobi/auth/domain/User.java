package skhu.easysobi.auth.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    private String nickname;

    @Column(unique = true)
    private Long kakaoId;

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<UserInventory> userInventoryList = new ArrayList<>(); // 식품 목록

}
