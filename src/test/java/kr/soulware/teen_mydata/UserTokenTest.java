package kr.soulware.teen_mydata;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Disabled
@SpringBootTest
public class UserTokenTest {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Test
    void generateLongTermToken() {
        String email = "testuser@example.com";
        String role = "USER";
        // 10년(3650일) 만료
        long expirationMs = 1000L * 60 * 60 * 24 * 365 * 10;
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        Key key = Keys.hmacShaKeyFor(bytes);
        String token = Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        System.out.println("[10년 만료 JWT 토큰] " + token);
    }
}
