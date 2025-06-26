package kr.soulware.teen_mydata.service;

import kr.soulware.teen_mydata.enums.ProfileType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class ProfileCheckService {

    private final Environment env;

    public boolean isActiveProfile(ProfileType... profiles) {
        return Arrays.stream(
            env.getActiveProfiles()
        ).anyMatch(
            active -> Arrays.stream(profiles).anyMatch(
                profile -> profile.getValue().equalsIgnoreCase(active)
            )
        );
    }

}
