package kr.soulware.teen_mydata.util;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class ProfileUtil {

    public static final String LOCAL = "local";
    public static final String DEV = "dev";
    public static final String PROD = "prod";

    private final Environment env;

    public boolean isActiveProfile(String... profiles) {
        return Arrays.stream(env.getActiveProfiles())
            .anyMatch(p -> Arrays.asList(profiles).contains(p));
    }

}
