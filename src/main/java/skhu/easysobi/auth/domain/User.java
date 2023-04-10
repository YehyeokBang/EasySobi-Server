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

    @NotBlank(message = "이메일은 꼭 필요해요")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "닉네임은 꼭 필요해요")
    private String nickname;

    @Column(unique = true)
    private Long kakaoId;

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<UserInventory> userInventoryList = new ArrayList<>(); // 식품 목록

    public User(String email, String nickname, Long kakaoId) {
        this.email = email;
        this.nickname = nickname;
        this.kakaoId = kakaoId;
    }

}
