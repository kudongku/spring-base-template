package kr.soulware.teen_mydata.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.soulware.teen_mydata.entity.User;
import kr.soulware.teen_mydata.repository.UserRepository;
import kr.soulware.teen_mydata.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        User user = userRepository.findByEmail(email).orElseThrow(
            RuntimeException::new
        );

        String jwt = jwtService.generateToken(user.getEmail(), user.getRole().name());

        Map<String, String> result = new HashMap<>();
        result.put("access_token", jwt);
        result.put("token_type", "Bearer");
        new ObjectMapper().writeValue(response.getWriter(), result);
    }

}
