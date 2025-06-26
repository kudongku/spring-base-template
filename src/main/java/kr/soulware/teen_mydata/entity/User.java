package kr.soulware.teen_mydata.entity;

import jakarta.persistence.*;
import kr.soulware.teen_mydata.enums.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User extends Timestamp {

    @Id
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String provider; // ex) google, kakao

    @Column(nullable = false)
    private String providerId; // ex) 구글 sub 값

    @Column
    private String profileImage; // 프로필 이미지 URL

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER; // 기본값 USER

    public User(String email, String name, String provider, String providerId, String profileImage) {
        this.email = email;
        this.name = name;
        this.provider = provider;
        this.providerId = providerId;
        this.profileImage = profileImage;
    }

}
