package kr.soulware.teen_mydata.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User {

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

    public User(String email, String name, String provider, String providerId, String profileImage) {
        this.email = email;
        this.name = name;
        this.provider = provider;
        this.providerId = providerId;
        this.profileImage = profileImage;
    }

    public User(String email, String name, String provider, String providerId) {
        this(email, name, provider, providerId, null);
    }

}
