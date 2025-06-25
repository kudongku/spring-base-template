package kr.soulware.teen_mydata.service;

import kr.soulware.teen_mydata.entity.User;
import kr.soulware.teen_mydata.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId(); // ex) google
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String providerId = (String) attributes.get("sub"); // 구글의 경우 sub
        String profileImage = (String) attributes.get("picture"); // 구글의 경우 프로필 이미지

        userRepository
            .findByEmail(email)
            .ifPresentOrElse(
                user -> {
                    user.setName(name);
                    user.setProfileImage(profileImage);
                    userRepository.save(user);
                },
                () -> userRepository.save(new User(email, name, provider, providerId, profileImage))
            );
        return oAuth2User;
    }

} 