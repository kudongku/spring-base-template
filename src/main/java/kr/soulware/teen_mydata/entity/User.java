package kr.soulware.teen_mydata.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

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

    @Column(nullable = true)
    private String profileImage; // 프로필 이미지 URL

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER; // 기본값 USER

    public User(String email, String name, String provider, String providerId, String profileImage, Role role) {
        this.email = email;
        this.name = name;
        this.provider = provider;
        this.providerId = providerId;
        this.profileImage = profileImage;
        this.role = role;
    }

    public User(String email, String name, String provider, String providerId, String profileImage) {
        this(email, name, provider, providerId, profileImage, Role.USER);
    }

}
